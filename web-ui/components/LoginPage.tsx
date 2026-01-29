'use client'

import { useState } from 'react'
import { authApi, profilesApi } from '@/lib/api'

interface LoginPageProps {
  onLogin: (session: { sessionId: string; role: string; profile?: any; account?: any }) => void
}

type LoginMode = 'employee' | 'customer' | 'atm'

export default function LoginPage({ onLogin }: LoginPageProps) {
  const [loginMode, setLoginMode] = useState<LoginMode>('employee')
  const [isSignUp, setIsSignUp] = useState(false)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [accountNumber, setAccountNumber] = useState('')
  const [pin, setPin] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  // Sign up fields
  const [signUpData, setSignUpData] = useState({
    name: '',
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    phone: '',
    address: '',
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      let response
      if (loginMode === 'employee') {
        response = await authApi.employeeLogin(username, password)
      } else if (loginMode === 'customer') {
        response = await authApi.customerLogin(username, password)
      } else {
        // ATM login
        response = await authApi.atmLogin(accountNumber, pin)
      }

      if (response.success) {
        onLogin({
          sessionId: response.sessionId,
          role: response.role,
          profile: response.profile,
          account: response.account,
        })
      } else {
        setError(response.error || 'Login failed')
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Connection error. Make sure the API server is running.')
    } finally {
      setLoading(false)
    }
  }

  const handleSignUp = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    // Validation
    if (signUpData.password !== signUpData.confirmPassword) {
      setError('Passwords do not match')
      return
    }

    if (signUpData.password.length < 4) {
      setError('Password must be at least 4 characters')
      return
    }

    if (!signUpData.username.trim()) {
      setError('Username is required')
      return
    }

    setLoading(true)

    try {
      const response = await profilesApi.createProfile({
        name: signUpData.name,
        username: signUpData.username,
        password: signUpData.password,
        phone: parseInt(signUpData.phone) || 0,
        address: signUpData.address,
        email: signUpData.email,
      })

      if (response.success) {
        setSuccess('Account created successfully! You can now sign in.')
        setSignUpData({
          name: '',
          username: '',
          password: '',
          confirmPassword: '',
          email: '',
          phone: '',
          address: '',
        })
        // Switch back to login after successful registration
        setTimeout(() => {
          setIsSignUp(false)
          setUsername(signUpData.username)
          setSuccess('')
        }, 2000)
      } else {
        setError(response.error || 'Failed to create account')
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to create account. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const resetForm = () => {
    setUsername('')
    setPassword('')
    setAccountNumber('')
    setPin('')
    setError('')
    setSuccess('')
    setSignUpData({
      name: '',
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      phone: '',
      address: '',
    })
  }

  // Sign Up Form
  if (isSignUp) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-primary-blue via-primary-dark to-primary-blue flex items-center justify-center p-4">
        <div className="bg-white rounded-lg shadow-2xl p-8 w-full max-w-md">
          <h1 className="text-3xl font-bold text-center mb-2 text-primary-blue">Create Account</h1>
          <p className="text-center text-gray-600 mb-6">Sign up as a new customer</p>

          <form onSubmit={handleSignUp} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Full Name *</label>
              <input
                type="text"
                value={signUpData.name}
                onChange={(e) => setSignUpData({ ...signUpData, name: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                placeholder="John Doe"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Username *</label>
              <input
                type="text"
                value={signUpData.username}
                onChange={(e) => setSignUpData({ ...signUpData, username: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                placeholder="johndoe"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Password *</label>
                <input
                  type="password"
                  value={signUpData.password}
                  onChange={(e) => setSignUpData({ ...signUpData, password: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                  placeholder="••••••"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Confirm *</label>
                <input
                  type="password"
                  value={signUpData.confirmPassword}
                  onChange={(e) => setSignUpData({ ...signUpData, confirmPassword: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                  placeholder="••••••"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
              <input
                type="email"
                value={signUpData.email}
                onChange={(e) => setSignUpData({ ...signUpData, email: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                placeholder="john@example.com"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number *</label>
              <input
                type="tel"
                value={signUpData.phone}
                onChange={(e) => setSignUpData({ ...signUpData, phone: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                placeholder="1234567890"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Address *</label>
              <input
                type="text"
                value={signUpData.address}
                onChange={(e) => setSignUpData({ ...signUpData, address: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                placeholder="123 Main St, City"
                required
              />
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
                {error}
              </div>
            )}

            {success && (
              <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
                {success}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-green-500 text-white py-3 rounded-lg font-semibold hover:bg-green-600 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Creating Account...' : 'Create Account'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <button
              onClick={() => {
                setIsSignUp(false)
                resetForm()
              }}
              className="text-primary-blue hover:underline font-medium"
            >
              ← Back to Sign In
            </button>
          </div>
        </div>
      </div>
    )
  }

  // Login Form
  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-blue via-primary-dark to-primary-blue flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-2xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-center mb-2 text-primary-blue">Banking System</h1>
        <p className="text-center text-gray-600 mb-6">Sign in to continue</p>

        {/* Login Mode Tabs */}
        <div className="flex mb-6 bg-gray-100 rounded-lg p-1">
          <button
            onClick={() => {
              setLoginMode('employee')
              resetForm()
            }}
            className={`flex-1 py-2 px-2 rounded-md font-medium transition text-sm ${
              loginMode === 'employee'
                ? 'bg-primary-blue text-white shadow'
                : 'text-gray-600 hover:text-primary-blue'
            }`}
          >
            Employee
          </button>
          <button
            onClick={() => {
              setLoginMode('customer')
              resetForm()
            }}
            className={`flex-1 py-2 px-2 rounded-md font-medium transition text-sm ${
              loginMode === 'customer'
                ? 'bg-primary-blue text-white shadow'
                : 'text-gray-600 hover:text-primary-blue'
            }`}
          >
            Customer
          </button>
          <button
            onClick={() => {
              setLoginMode('atm')
              resetForm()
            }}
            className={`flex-1 py-2 px-2 rounded-md font-medium transition text-sm ${
              loginMode === 'atm'
                ? 'bg-green-500 text-white shadow'
                : 'text-gray-600 hover:text-green-500'
            }`}
          >
            ATM
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {loginMode === 'atm' ? (
            <>
              {/* ATM Login Fields */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Account Number
                </label>
                <input
                  type="text"
                  value={accountNumber}
                  onChange={(e) => setAccountNumber(e.target.value.replace(/\D/g, ''))}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  placeholder="Enter your account number"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  PIN
                </label>
                <input
                  type="password"
                  maxLength={4}
                  value={pin}
                  onChange={(e) => setPin(e.target.value.replace(/\D/g, ''))}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  placeholder="••••"
                  required
                />
              </div>
            </>
          ) : (
            <>
              {/* Employee/Customer Login Fields */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Username
                </label>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Password
                </label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
                  required
                />
              </div>
            </>
          )}

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className={`w-full text-white py-3 rounded-lg font-semibold transition disabled:opacity-50 disabled:cursor-not-allowed ${
              loginMode === 'atm'
                ? 'bg-green-500 hover:bg-green-600'
                : 'bg-primary-red hover:bg-red-600'
            }`}
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        {/* Sign Up option for customers only */}
        {loginMode === 'customer' && (
          <div className="mt-6 text-center border-t pt-6">
            <p className="text-gray-600 mb-3">Don't have an account?</p>
            <button
              onClick={() => {
                setIsSignUp(true)
                resetForm()
              }}
              className="w-full bg-primary-blue text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
            >
              Create New Account
            </button>
          </div>
        )}

        {/* Help text */}
        <p className="mt-6 text-center text-sm text-gray-500">
          {loginMode === 'employee' && 'Employee credentials required'}
          {loginMode === 'customer' && 'Demo: user1/pass1'}
          {loginMode === 'atm' && 'Enter your account number and 4-digit PIN'}
        </p>
      </div>
    </div>
  )
}
