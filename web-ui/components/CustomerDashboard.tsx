'use client'

import { useState, useEffect } from 'react'
import { accountsApi } from '@/lib/api'

interface CustomerDashboardProps {
  session: { sessionId: string; role: string; profile?: any }
  onLogout: () => void
}

interface AccountData {
  accountNumber: number
  pin: string
  type: string
  balance: number
}

export default function CustomerDashboard({ session, onLogout }: CustomerDashboardProps) {
  const [accounts, setAccounts] = useState<AccountData[]>([])
  const [selectedAccount, setSelectedAccount] = useState<AccountData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refreshKey, setRefreshKey] = useState(0)

  // Transaction state
  const [transactionType, setTransactionType] = useState<'deposit' | 'withdraw'>('deposit')
  const [amount, setAmount] = useState('')
  const [transactionMessage, setTransactionMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)
  const [transactionLoading, setTransactionLoading] = useState(false)

  // PIN change state
  const [showPinChange, setShowPinChange] = useState(false)
  const [newPin, setNewPin] = useState('')
  const [pinMessage, setPinMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  useEffect(() => {
    loadAccounts()
  }, [refreshKey])

  const loadAccounts = async () => {
    try {
      setError('')
      const response = await accountsApi.getAccounts()
      const accountsList = Array.isArray(response.accounts) ? response.accounts : []
      setAccounts(accountsList)
      
      if (accountsList.length > 0 && !selectedAccount) {
        setSelectedAccount(accountsList[0])
      } else if (selectedAccount) {
        const updated = accountsList.find((a: AccountData) => a.accountNumber === selectedAccount.accountNumber)
        if (updated) setSelectedAccount(updated)
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to load accounts')
    } finally {
      setLoading(false)
    }
  }

  const handleTransaction = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedAccount) return

    const amountNum = parseFloat(amount)
    if (isNaN(amountNum) || amountNum <= 0) {
      setTransactionMessage({ type: 'error', text: 'Please enter a valid amount' })
      return
    }

    setTransactionLoading(true)
    setTransactionMessage(null)

    try {
      const response =
        transactionType === 'deposit'
          ? await accountsApi.deposit(selectedAccount.accountNumber.toString(), amountNum)
          : await accountsApi.withdraw(selectedAccount.accountNumber.toString(), amountNum)

      if (response.success) {
        setTransactionMessage({ type: 'success', text: response.message || 'Transaction successful!' })
        setAmount('')
        setRefreshKey((prev) => prev + 1)
      } else {
        setTransactionMessage({ type: 'error', text: response.error || 'Transaction failed' })
      }
    } catch (err: any) {
      setTransactionMessage({ type: 'error', text: err.response?.data?.error || 'Transaction failed' })
    } finally {
      setTransactionLoading(false)
    }
  }

  const handlePinChange = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedAccount) return

    if (newPin.length !== 4 || !/^\d+$/.test(newPin)) {
      setPinMessage({ type: 'error', text: 'PIN must be exactly 4 digits' })
      return
    }

    setTransactionLoading(true)
    setPinMessage(null)

    try {
      const response = await accountsApi.updatePin(selectedAccount.accountNumber.toString(), newPin)
      if (response.success) {
        setPinMessage({ type: 'success', text: 'PIN updated successfully!' })
        setNewPin('')
        setShowPinChange(false)
        setRefreshKey((prev) => prev + 1)
      } else {
        setPinMessage({ type: 'error', text: response.error || 'Failed to update PIN' })
      }
    } catch (err: any) {
      setPinMessage({ type: 'error', text: err.response?.data?.error || 'Failed to update PIN' })
    } finally {
      setTransactionLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-xl text-gray-600">Loading your accounts...</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top Navigation */}
      <nav className="bg-primary-blue text-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-xl font-bold">My Banking</h1>
            <div className="flex items-center gap-4">
              <span className="text-sm">Welcome, {session.profile?.name || session.profile?.username || 'Customer'}</span>
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

      <div className="max-w-6xl mx-auto px-4 py-8">
        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        )}

        {accounts.length === 0 ? (
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <div className="text-6xl mb-4">🏦</div>
            <h3 className="text-xl font-semibold text-gray-700 mb-2">No Accounts Found</h3>
            <p className="text-gray-500">You don't have any accounts linked to your profile yet.</p>
            <p className="text-gray-500 mt-2">Please contact a bank employee to set up your account.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Accounts List */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-lg font-bold text-gray-800 mb-4">My Accounts</h2>
                <div className="space-y-3">
                  {accounts.map((account) => (
                    <button
                      key={account.accountNumber}
                      onClick={() => {
                        setSelectedAccount(account)
                        setTransactionMessage(null)
                        setPinMessage(null)
                        setShowPinChange(false)
                      }}
                      className={`w-full text-left p-4 rounded-lg border-2 transition ${
                        selectedAccount?.accountNumber === account.accountNumber
                          ? 'border-primary-blue bg-blue-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <div className="flex justify-between items-center mb-1">
                        <span className="font-semibold text-gray-800">#{account.accountNumber}</span>
                        <span className="text-xs bg-primary-blue text-white px-2 py-1 rounded-full capitalize">
                          {account.type}
                        </span>
                      </div>
                      <div className="text-xl font-bold text-green-600">
                        ${account.balance.toFixed(2)}
                      </div>
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Selected Account Details & Actions */}
            <div className="lg:col-span-2 space-y-6">
              {selectedAccount ? (
                <>
                  {/* Account Details Card */}
                  <div className="bg-white rounded-lg shadow-md p-6">
                    <div className="flex justify-between items-start mb-6">
                      <div>
                        <h2 className="text-2xl font-bold text-gray-800">Account #{selectedAccount.accountNumber}</h2>
                        <span className="text-sm text-gray-500 capitalize">{selectedAccount.type} Account</span>
                      </div>
                      <div className="text-right">
                        <span className="text-sm text-gray-500 block">Current Balance</span>
                        <span className="text-3xl font-bold text-green-600">${selectedAccount.balance.toFixed(2)}</span>
                      </div>
                    </div>

                    <button
                      onClick={() => setShowPinChange(!showPinChange)}
                      className="text-primary-blue hover:text-blue-700 font-medium text-sm"
                    >
                      {showPinChange ? '← Back to Account' : 'Change PIN →'}
                    </button>

                    {/* PIN Change Form */}
                    {showPinChange && (
                      <form onSubmit={handlePinChange} className="mt-4 p-4 bg-blue-50 rounded-lg">
                        <h3 className="font-semibold text-gray-800 mb-3">Change PIN</h3>
                        <div className="flex gap-4 items-end">
                          <div className="flex-1">
                            <label className="block text-sm font-medium text-gray-700 mb-1">New PIN (4 digits)</label>
                            <input
                              type="password"
                              maxLength={4}
                              value={newPin}
                              onChange={(e) => setNewPin(e.target.value.replace(/\D/g, ''))}
                              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                              placeholder="••••"
                              required
                            />
                          </div>
                          <button
                            type="submit"
                            disabled={transactionLoading}
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
                  </div>

                  {/* Transaction Form */}
                  {!showPinChange && (
                    <div className="bg-white rounded-lg shadow-md p-6">
                      <h2 className="text-xl font-bold text-gray-800 mb-4">Make a Transaction</h2>

                      {/* Transaction Type Toggle */}
                      <div className="flex gap-2 mb-6 bg-gray-100 rounded-lg p-1">
                        <button
                          type="button"
                          onClick={() => setTransactionType('deposit')}
                          className={`flex-1 py-3 px-4 rounded-md font-semibold transition ${
                            transactionType === 'deposit'
                              ? 'bg-green-500 text-white shadow'
                              : 'text-gray-600 hover:text-gray-800'
                          }`}
                        >
                          💰 Deposit
                        </button>
                        <button
                          type="button"
                          onClick={() => setTransactionType('withdraw')}
                          className={`flex-1 py-3 px-4 rounded-md font-semibold transition ${
                            transactionType === 'withdraw'
                              ? 'bg-red-500 text-white shadow'
                              : 'text-gray-600 hover:text-gray-800'
                          }`}
                        >
                          💸 Withdraw
                        </button>
                      </div>

                      <form onSubmit={handleTransaction} className="space-y-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">Amount ($)</label>
                          <input
                            type="number"
                            step="0.01"
                            min="0.01"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent text-xl"
                            placeholder="0.00"
                            required
                          />
                        </div>

                        {transactionMessage && (
                          <div
                            className={`px-4 py-3 rounded-lg ${
                              transactionMessage.type === 'success'
                                ? 'bg-green-50 border border-green-200 text-green-700'
                                : 'bg-red-50 border border-red-200 text-red-700'
                            }`}
                          >
                            {transactionMessage.text}
                          </div>
                        )}

                        <button
                          type="submit"
                          disabled={transactionLoading}
                          className={`w-full py-4 rounded-lg font-bold text-lg transition disabled:opacity-50 disabled:cursor-not-allowed ${
                            transactionType === 'deposit'
                              ? 'bg-green-500 hover:bg-green-600 text-white'
                              : 'bg-red-500 hover:bg-red-600 text-white'
                          }`}
                        >
                          {transactionLoading
                            ? 'Processing...'
                            : transactionType === 'deposit'
                            ? 'Deposit Funds'
                            : 'Withdraw Funds'}
                        </button>
                      </form>
                    </div>
                  )}
                </>
              ) : (
                <div className="bg-white rounded-lg shadow-md p-12 text-center">
                  <p className="text-gray-500">Select an account from the list to view details and make transactions.</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Profile Info */}
        {session.profile && (
          <div className="mt-8 bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-bold text-gray-800 mb-4">Profile Information</h2>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <span className="text-sm text-gray-500 block">Name</span>
                <span className="font-medium">{session.profile.name || 'N/A'}</span>
              </div>
              <div>
                <span className="text-sm text-gray-500 block">Email</span>
                <span className="font-medium">{session.profile.email || 'N/A'}</span>
              </div>
              <div>
                <span className="text-sm text-gray-500 block">Phone</span>
                <span className="font-medium">{session.profile.phone || 'N/A'}</span>
              </div>
              <div>
                <span className="text-sm text-gray-500 block">Address</span>
                <span className="font-medium">{session.profile.address || 'N/A'}</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
