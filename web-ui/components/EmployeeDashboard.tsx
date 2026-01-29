'use client'

import { useState } from 'react'
import { accountsApi, profilesApi } from '@/lib/api'
import CreateAccountForm from './CreateAccountForm'

interface EmployeeDashboardProps {
  session: { sessionId: string; role: string }
  onLogout: () => void
}

interface AccountData {
  accountNumber: number
  pin: string
  type: string
  balance: number
}

interface ProfileData {
  username: string
  name: string
  phone: string
  address: string
  email: string
  creditScore: string
}

export default function EmployeeDashboard({ session, onLogout }: EmployeeDashboardProps) {
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResult, setSearchResult] = useState<{
    account: AccountData
    profile: ProfileData | null
  } | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [showCreateAccount, setShowCreateAccount] = useState(false)
  const [showCreateProfile, setShowCreateProfile] = useState(false)
  const [showLinkAccount, setShowLinkAccount] = useState(false)

  // PIN change state
  const [showPinUpdate, setShowPinUpdate] = useState(false)
  const [newPin, setNewPin] = useState('')
  const [pinMessage, setPinMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  // Link account state
  const [linkUsername, setLinkUsername] = useState('')
  const [linkMessage, setLinkMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  // Create profile state
  const [newProfile, setNewProfile] = useState({
    name: '',
    username: '',
    password: '',
    phone: '',
    address: '',
    email: '',
  })
  const [profileMessage, setProfileMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  // Edit profile state
  const [showEditProfile, setShowEditProfile] = useState(false)
  const [editProfileData, setEditProfileData] = useState({
    name: '',
    phone: '',
    address: '',
    email: '',
    creditScore: '',
    password: '',
  })
  const [editProfileMessage, setEditProfileMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!searchQuery.trim()) return

    setLoading(true)
    setError('')
    setSearchResult(null)
    setShowPinUpdate(false)
    setPinMessage(null)
    setShowLinkAccount(false)
    setLinkMessage(null)

    try {
      const result = await accountsApi.searchAccount(searchQuery.trim())
      if (result.found) {
        setSearchResult({
          account: result.account,
          profile: result.profile,
        })
      } else {
        setError('Account not found')
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to search. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleUpdatePin = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!searchResult || newPin.length !== 4 || !/^\d+$/.test(newPin)) {
      setPinMessage({ type: 'error', text: 'PIN must be exactly 4 digits' })
      return
    }

    setLoading(true)
    setPinMessage(null)

    try {
      const response = await accountsApi.updatePin(searchResult.account.accountNumber.toString(), newPin)
      if (response.success) {
        setPinMessage({ type: 'success', text: 'PIN updated successfully!' })
        setNewPin('')
        setShowPinUpdate(false)
        // Refresh search result
        const result = await accountsApi.searchAccount(searchResult.account.accountNumber.toString())
        if (result.found) {
          setSearchResult({ account: result.account, profile: result.profile })
        }
      } else {
        setPinMessage({ type: 'error', text: response.error || 'Failed to update PIN' })
      }
    } catch (err: any) {
      setPinMessage({ type: 'error', text: err.response?.data?.error || 'Failed to update PIN' })
    } finally {
      setLoading(false)
    }
  }

  const handleLinkAccount = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!searchResult || !linkUsername.trim()) return

    setLoading(true)
    setLinkMessage(null)

    try {
      const response = await accountsApi.linkAccountToProfile(
        searchResult.account.accountNumber.toString(),
        linkUsername.trim()
      )
      if (response.success) {
        setLinkMessage({ type: 'success', text: 'Account linked successfully!' })
        setLinkUsername('')
        setShowLinkAccount(false)
        // Refresh search result
        const result = await accountsApi.searchAccount(searchResult.account.accountNumber.toString())
        if (result.found) {
          setSearchResult({ account: result.account, profile: result.profile })
        }
      } else {
        setLinkMessage({ type: 'error', text: response.error || 'Failed to link account' })
      }
    } catch (err: any) {
      setLinkMessage({ type: 'error', text: err.response?.data?.error || 'Failed to link account' })
    } finally {
      setLoading(false)
    }
  }

  const handleCreateProfile = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setProfileMessage(null)

    try {
      const response = await profilesApi.createProfile({
        name: newProfile.name,
        username: newProfile.username,
        password: newProfile.password,
        phone: parseInt(newProfile.phone) || 0,
        address: newProfile.address,
        email: newProfile.email,
      })
      if (response.success) {
        setProfileMessage({ type: 'success', text: 'Profile created successfully!' })
        setNewProfile({ name: '', username: '', password: '', phone: '', address: '', email: '' })
        setShowCreateProfile(false)
      } else {
        setProfileMessage({ type: 'error', text: response.error || 'Failed to create profile' })
      }
    } catch (err: any) {
      setProfileMessage({ type: 'error', text: err.response?.data?.error || 'Failed to create profile' })
    } finally {
      setLoading(false)
    }
  }

  const handleEditProfileClick = () => {
    if (searchResult?.profile) {
      setEditProfileData({
        name: searchResult.profile.name || '',
        phone: searchResult.profile.phone || '',
        address: searchResult.profile.address || '',
        email: searchResult.profile.email || '',
        creditScore: searchResult.profile.creditScore || '0',
        password: '',
      })
      setShowEditProfile(true)
      setEditProfileMessage(null)
    }
  }

  const handleUpdateProfileInfo = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!searchResult?.profile) return

    setLoading(true)
    setEditProfileMessage(null)

    try {
      const response = await profilesApi.updateProfile({
        username: searchResult.profile.username,
        name: editProfileData.name,
        phone: editProfileData.phone,
        address: editProfileData.address,
        email: editProfileData.email,
        creditScore: editProfileData.creditScore,
        password: editProfileData.password || undefined,
      })
      if (response.success) {
        setEditProfileMessage({ type: 'success', text: 'Profile updated successfully!' })
        // Refresh search result
        const result = await accountsApi.searchAccount(searchResult.account.accountNumber.toString())
        if (result.found) {
          setSearchResult({ account: result.account, profile: result.profile })
        }
        setTimeout(() => {
          setShowEditProfile(false)
          setEditProfileMessage(null)
        }, 1500)
      } else {
        setEditProfileMessage({ type: 'error', text: response.error || 'Failed to update profile' })
      }
    } catch (err: any) {
      setEditProfileMessage({ type: 'error', text: err.response?.data?.error || 'Failed to update profile' })
    } finally {
      setLoading(false)
    }
  }

  const handleAccountCreated = () => {
    setShowCreateAccount(false)
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top Navigation */}
      <nav className="bg-primary-blue text-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-xl font-bold">Employee Dashboard</h1>
            <div className="flex items-center gap-4">
              <span className="text-sm">Employee</span>
              <button
                onClick={onLogout}
                className="bg-primary-red hover:bg-red-600 px-4 py-2 rounded-lg text-sm font-medium transition"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* Action Buttons */}
        <div className="flex gap-4 mb-8">
          <button
            onClick={() => {
              setShowCreateAccount(!showCreateAccount)
              setShowCreateProfile(false)
            }}
            className={`px-6 py-3 rounded-lg font-medium transition ${
              showCreateAccount
                ? 'bg-gray-300 text-gray-700'
                : 'bg-green-500 hover:bg-green-600 text-white'
            }`}
          >
            {showCreateAccount ? 'Cancel' : '+ Create Account'}
          </button>
          <button
            onClick={() => {
              setShowCreateProfile(!showCreateProfile)
              setShowCreateAccount(false)
            }}
            className={`px-6 py-3 rounded-lg font-medium transition ${
              showCreateProfile
                ? 'bg-gray-300 text-gray-700'
                : 'bg-blue-500 hover:bg-blue-600 text-white'
            }`}
          >
            {showCreateProfile ? 'Cancel' : '+ Create Profile'}
          </button>
        </div>

        {/* Create Account Form */}
        {showCreateAccount && (
          <div className="mb-8">
            <CreateAccountForm onAccountCreated={handleAccountCreated} />
          </div>
        )}

        {/* Create Profile Form */}
        {showCreateProfile && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8">
            <h2 className="text-xl font-bold text-gray-800 mb-4">Create New Profile</h2>
            <form onSubmit={handleCreateProfile} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                  <input
                    type="text"
                    value={newProfile.name}
                    onChange={(e) => setNewProfile({ ...newProfile, name: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                  <input
                    type="text"
                    value={newProfile.username}
                    onChange={(e) => setNewProfile({ ...newProfile, username: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                  <input
                    type="password"
                    value={newProfile.password}
                    onChange={(e) => setNewProfile({ ...newProfile, password: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
                  <input
                    type="tel"
                    value={newProfile.phone}
                    onChange={(e) => setNewProfile({ ...newProfile, phone: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <input
                    type="email"
                    value={newProfile.email}
                    onChange={(e) => setNewProfile({ ...newProfile, email: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                  <input
                    type="text"
                    value={newProfile.address}
                    onChange={(e) => setNewProfile({ ...newProfile, address: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                    required
                  />
                </div>
              </div>

              {profileMessage && (
                <div
                  className={`px-4 py-3 rounded-lg ${
                    profileMessage.type === 'success'
                      ? 'bg-green-50 border border-green-200 text-green-700'
                      : 'bg-red-50 border border-red-200 text-red-700'
                  }`}
                >
                  {profileMessage.text}
                </div>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-primary-blue hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition disabled:opacity-50"
              >
                {loading ? 'Creating...' : 'Create Profile'}
              </button>
            </form>
          </div>
        )}

        {/* Search Section */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Search Account</h2>
          <form onSubmit={handleSearch} className="flex gap-4">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Enter account number..."
              className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent text-lg"
            />
            <button
              type="submit"
              disabled={loading}
              className="bg-primary-blue hover:bg-blue-700 text-white px-8 py-3 rounded-lg font-semibold transition disabled:opacity-50"
            >
              {loading ? 'Searching...' : 'Search'}
            </button>
          </form>

          {error && (
            <div className="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}
        </div>

        {/* Search Results */}
        {searchResult && (
          <div className="space-y-6">
            {/* Account Details */}
            <div className="bg-white rounded-lg shadow-md p-6">
              <h2 className="text-xl font-bold text-gray-800 mb-4">Account Information</h2>
              <div className="grid grid-cols-2 gap-4">
                <div className="p-4 bg-gray-50 rounded-lg">
                  <span className="text-sm text-gray-500 block">Account Number</span>
                  <span className="text-2xl font-bold text-primary-blue">{searchResult.account.accountNumber}</span>
                </div>
                <div className="p-4 bg-gray-50 rounded-lg">
                  <span className="text-sm text-gray-500 block">Account Type</span>
                  <span className="text-xl font-semibold text-gray-800 capitalize">{searchResult.account.type}</span>
                </div>
                <div className="p-4 bg-gray-50 rounded-lg">
                  <span className="text-sm text-gray-500 block">Current Balance</span>
                  <span className="text-2xl font-bold text-green-600">${searchResult.account.balance.toFixed(2)}</span>
                </div>
                <div className="p-4 bg-gray-50 rounded-lg">
                  <span className="text-sm text-gray-500 block">PIN</span>
                  <span className="text-xl font-semibold text-gray-800">{searchResult.account.pin}</span>
                </div>
              </div>

              {/* Actions */}
              <div className="mt-6 flex gap-4">
                <button
                  onClick={() => {
                    setShowPinUpdate(!showPinUpdate)
                    setShowLinkAccount(false)
                  }}
                  className="flex-1 bg-primary-blue hover:bg-blue-700 text-white py-3 rounded-lg font-medium transition"
                >
                  {showPinUpdate ? 'Cancel PIN Change' : 'Change PIN'}
                </button>
                {!searchResult.profile && (
                  <button
                    onClick={() => {
                      setShowLinkAccount(!showLinkAccount)
                      setShowPinUpdate(false)
                    }}
                    className="flex-1 bg-orange-500 hover:bg-orange-600 text-white py-3 rounded-lg font-medium transition"
                  >
                    {showLinkAccount ? 'Cancel' : 'Link to Profile'}
                  </button>
                )}
              </div>

              {/* PIN Update Form */}
              {showPinUpdate && (
                <form onSubmit={handleUpdatePin} className="mt-4 p-4 bg-blue-50 rounded-lg">
                  <div className="flex gap-4 items-end">
                    <div className="flex-1">
                      <label className="block text-sm font-medium text-gray-700 mb-1">New PIN (4 digits)</label>
                      <input
                        type="text"
                        maxLength={4}
                        value={newPin}
                        onChange={(e) => setNewPin(e.target.value.replace(/\D/g, ''))}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        placeholder="0000"
                        required
                      />
                    </div>
                    <button
                      type="submit"
                      disabled={loading}
                      className="bg-primary-blue hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium transition disabled:opacity-50"
                    >
                      Update
                    </button>
                  </div>
                  {pinMessage && (
                    <div
                      className={`mt-3 px-4 py-2 rounded-lg ${
                        pinMessage.type === 'success'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {pinMessage.text}
                    </div>
                  )}
                </form>
              )}

              {/* Link Account Form */}
              {showLinkAccount && (
                <form onSubmit={handleLinkAccount} className="mt-4 p-4 bg-orange-50 rounded-lg">
                  <div className="flex gap-4 items-end">
                    <div className="flex-1">
                      <label className="block text-sm font-medium text-gray-700 mb-1">Username to link</label>
                      <input
                        type="text"
                        value={linkUsername}
                        onChange={(e) => setLinkUsername(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        placeholder="Enter username..."
                        required
                      />
                    </div>
                    <button
                      type="submit"
                      disabled={loading}
                      className="bg-orange-500 hover:bg-orange-600 text-white px-6 py-2 rounded-lg font-medium transition disabled:opacity-50"
                    >
                      Link
                    </button>
                  </div>
                  {linkMessage && (
                    <div
                      className={`mt-3 px-4 py-2 rounded-lg ${
                        linkMessage.type === 'success'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {linkMessage.text}
                    </div>
                  )}
                </form>
              )}
            </div>

            {/* Profile Details */}
            {searchResult.profile ? (
              <div className="bg-white rounded-lg shadow-md p-6">
                <div className="flex justify-between items-center mb-4">
                  <h2 className="text-xl font-bold text-gray-800">Linked Profile Information</h2>
                  <button
                    onClick={() => {
                      if (showEditProfile) {
                        setShowEditProfile(false)
                        setEditProfileMessage(null)
                      } else {
                        handleEditProfileClick()
                      }
                    }}
                    className={`px-4 py-2 rounded-lg font-medium transition ${
                      showEditProfile
                        ? 'bg-gray-300 text-gray-700 hover:bg-gray-400'
                        : 'bg-purple-500 text-white hover:bg-purple-600'
                    }`}
                  >
                    {showEditProfile ? 'Cancel Edit' : 'Edit Profile'}
                  </button>
                </div>

                {showEditProfile ? (
                  <form onSubmit={handleUpdateProfileInfo} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                        <input
                          type="text"
                          value={editProfileData.name}
                          onChange={(e) => setEditProfileData({ ...editProfileData, name: e.target.value })}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Username (read-only)</label>
                        <input
                          type="text"
                          value={searchResult.profile.username}
                          disabled
                          className="w-full px-4 py-2 border border-gray-200 rounded-lg bg-gray-100 text-gray-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                        <input
                          type="email"
                          value={editProfileData.email}
                          onChange={(e) => setEditProfileData({ ...editProfileData, email: e.target.value })}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
                        <input
                          type="tel"
                          value={editProfileData.phone}
                          onChange={(e) => setEditProfileData({ ...editProfileData, phone: e.target.value })}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                        <input
                          type="text"
                          value={editProfileData.address}
                          onChange={(e) => setEditProfileData({ ...editProfileData, address: e.target.value })}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Credit Score</label>
                        <input
                          type="number"
                          value={editProfileData.creditScore}
                          onChange={(e) => setEditProfileData({ ...editProfileData, creditScore: e.target.value })}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        />
                      </div>
                      <div className="col-span-2">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          New Password (leave blank to keep current)
                        </label>
                        <input
                          type="password"
                          value={editProfileData.password}
                          onChange={(e) => setEditProfileData({ ...editProfileData, password: e.target.value })}
                          placeholder="Enter new password..."
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                        />
                      </div>
                    </div>

                    {editProfileMessage && (
                      <div
                        className={`px-4 py-3 rounded-lg ${
                          editProfileMessage.type === 'success'
                            ? 'bg-green-50 border border-green-200 text-green-700'
                            : 'bg-red-50 border border-red-200 text-red-700'
                        }`}
                      >
                        {editProfileMessage.text}
                      </div>
                    )}

                    <button
                      type="submit"
                      disabled={loading}
                      className="w-full bg-purple-500 hover:bg-purple-600 text-white py-3 rounded-lg font-semibold transition disabled:opacity-50"
                    >
                      {loading ? 'Saving...' : 'Save Changes'}
                    </button>
                  </form>
                ) : (
                  <div className="grid grid-cols-2 gap-4">
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <span className="text-sm text-gray-500 block">Name</span>
                      <span className="text-lg font-semibold text-gray-800">{searchResult.profile.name || 'N/A'}</span>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <span className="text-sm text-gray-500 block">Username</span>
                      <span className="text-lg font-semibold text-gray-800">{searchResult.profile.username}</span>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <span className="text-sm text-gray-500 block">Email</span>
                      <span className="text-lg font-semibold text-gray-800">{searchResult.profile.email || 'N/A'}</span>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <span className="text-sm text-gray-500 block">Phone</span>
                      <span className="text-lg font-semibold text-gray-800">{searchResult.profile.phone || 'N/A'}</span>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg col-span-2">
                      <span className="text-sm text-gray-500 block">Address</span>
                      <span className="text-lg font-semibold text-gray-800">{searchResult.profile.address || 'N/A'}</span>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <span className="text-sm text-gray-500 block">Credit Score</span>
                      <span className="text-lg font-semibold text-gray-800">{searchResult.profile.creditScore || '0'}</span>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6 text-center">
                <p className="text-yellow-700 font-medium">This account is not linked to any profile.</p>
                <p className="text-yellow-600 text-sm mt-1">Use the "Link to Profile" button above to associate it with a customer.</p>
              </div>
            )}
          </div>
        )}

        {/* Initial State */}
        {!searchResult && !showCreateAccount && !showCreateProfile && !error && (
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <div className="text-6xl mb-4">🔍</div>
            <h3 className="text-xl font-semibold text-gray-700 mb-2">Search for an Account</h3>
            <p className="text-gray-500">Enter an account number above to view and manage account details.</p>
          </div>
        )}
      </div>
    </div>
  )
}
