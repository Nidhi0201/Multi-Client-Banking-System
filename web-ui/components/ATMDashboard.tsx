'use client'

import { useState, useEffect } from 'react'
import { accountsApi } from '@/lib/api'

interface ATMDashboardProps {
  session: { sessionId: string; role: string; account?: any }
  onLogout: () => void
}

interface AccountData {
  accountNumber: number
  pin: string
  type: string
  balance: number
}

export default function ATMDashboard({ session, onLogout }: ATMDashboardProps) {
  const [account, setAccount] = useState<AccountData | null>(session.account || null)
  const [loading, setLoading] = useState(!session.account)
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
    if (!session.account) {
      loadAccount()
    }
  }, [refreshKey])

  const loadAccount = async () => {
    try {
      const response = await accountsApi.getAccounts()
      const accounts = Array.isArray(response.accounts) ? response.accounts : []
      if (accounts.length > 0) {
        setAccount(accounts[0])
      }
    } catch (err: any) {
      console.error('Failed to load account:', err)
    } finally {
      setLoading(false)
    }
  }

  const refreshAccount = async () => {
    try {
      const response = await accountsApi.getAccounts()
      const accounts = Array.isArray(response.accounts) ? response.accounts : []
      if (accounts.length > 0) {
        setAccount(accounts[0])
      }
    } catch (err: any) {
      console.error('Failed to refresh account:', err)
    }
  }

  const handleTransaction = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!account) return

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
          ? await accountsApi.deposit(account.accountNumber.toString(), amountNum)
          : await accountsApi.withdraw(account.accountNumber.toString(), amountNum)

      if (response.success) {
        setTransactionMessage({ type: 'success', text: response.message || 'Transaction successful!' })
        setAmount('')
        await refreshAccount()
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
    if (!account) return

    if (newPin.length !== 4 || !/^\d+$/.test(newPin)) {
      setPinMessage({ type: 'error', text: 'PIN must be exactly 4 digits' })
      return
    }

    setTransactionLoading(true)
    setPinMessage(null)

    try {
      const response = await accountsApi.updatePin(account.accountNumber.toString(), newPin)
      if (response.success) {
        setPinMessage({ type: 'success', text: 'PIN updated successfully!' })
        setNewPin('')
        setShowPinChange(false)
        await refreshAccount()
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
      <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 flex items-center justify-center">
        <div className="text-xl text-white">Loading ATM...</div>
      </div>
    )
  }

  if (!account) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 flex items-center justify-center">
        <div className="bg-white rounded-lg shadow-2xl p-8 text-center">
          <div className="text-6xl mb-4">❌</div>
          <h2 className="text-xl font-bold text-gray-800 mb-2">Account Not Found</h2>
          <p className="text-gray-600 mb-4">Unable to load account information.</p>
          <button
            onClick={onLogout}
            className="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded-lg font-medium transition"
          >
            Exit ATM
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 p-4">
      {/* ATM Header */}
      <div className="max-w-2xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <div className="flex items-center gap-3">
            <div className="text-4xl">🏧</div>
            <div>
              <h1 className="text-2xl font-bold text-white">ATM Service</h1>
              <p className="text-green-200 text-sm">Account #{account.accountNumber}</p>
            </div>
          </div>
          <button
            onClick={onLogout}
            className="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded-lg font-medium transition"
          >
            Exit
          </button>
        </div>

        {/* Account Balance Card */}
        <div className="bg-white rounded-2xl shadow-2xl p-8 mb-6">
          <div className="text-center mb-6">
            <span className="text-sm text-gray-500 block mb-1">Available Balance</span>
            <span className="text-5xl font-bold text-green-600">${account.balance.toFixed(2)}</span>
            <div className="mt-2">
              <span className="bg-green-100 text-green-700 text-sm px-3 py-1 rounded-full capitalize">
                {account.type} Account
              </span>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="flex gap-3 mb-6">
            <button
              onClick={() => setShowPinChange(!showPinChange)}
              className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-3 rounded-lg font-medium transition"
            >
              {showPinChange ? '← Back' : '🔐 Change PIN'}
            </button>
            <button
              onClick={refreshAccount}
              className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-3 rounded-lg font-medium transition"
            >
              🔄 Refresh
            </button>
          </div>

          {/* PIN Change Form */}
          {showPinChange && (
            <form onSubmit={handlePinChange} className="p-4 bg-blue-50 rounded-xl mb-6">
              <h3 className="font-semibold text-gray-800 mb-3">Change Your PIN</h3>
              <div className="flex gap-4 items-end">
                <div className="flex-1">
                  <label className="block text-sm font-medium text-gray-700 mb-1">New PIN (4 digits)</label>
                  <input
                    type="password"
                    maxLength={4}
                    value={newPin}
                    onChange={(e) => setNewPin(e.target.value.replace(/\D/g, ''))}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent text-center text-2xl tracking-widest"
                    placeholder="••••"
                    required
                  />
                </div>
                <button
                  type="submit"
                  disabled={transactionLoading}
                  className="bg-blue-500 hover:bg-blue-600 text-white px-8 py-3 rounded-lg font-medium transition disabled:opacity-50"
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

          {/* Transaction Form */}
          {!showPinChange && (
            <>
              {/* Transaction Type Toggle */}
              <div className="flex gap-2 mb-6 bg-gray-100 rounded-xl p-2">
                <button
                  type="button"
                  onClick={() => {
                    setTransactionType('deposit')
                    setTransactionMessage(null)
                  }}
                  className={`flex-1 py-4 px-4 rounded-lg font-bold text-lg transition ${
                    transactionType === 'deposit'
                      ? 'bg-green-500 text-white shadow-lg'
                      : 'text-gray-600 hover:text-gray-800'
                  }`}
                >
                  💰 Deposit
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setTransactionType('withdraw')
                    setTransactionMessage(null)
                  }}
                  className={`flex-1 py-4 px-4 rounded-lg font-bold text-lg transition ${
                    transactionType === 'withdraw'
                      ? 'bg-orange-500 text-white shadow-lg'
                      : 'text-gray-600 hover:text-gray-800'
                  }`}
                >
                  💸 Withdraw
                </button>
              </div>

              <form onSubmit={handleTransaction} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Enter Amount
                  </label>
                  <div className="relative">
                    <span className="absolute left-4 top-1/2 -translate-y-1/2 text-3xl text-gray-400">$</span>
                    <input
                      type="number"
                      step="0.01"
                      min="0.01"
                      value={amount}
                      onChange={(e) => setAmount(e.target.value)}
                      className="w-full pl-12 pr-4 py-4 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent text-3xl font-bold text-center"
                      placeholder="0.00"
                      required
                    />
                  </div>
                </div>

                {/* Quick Amount Buttons */}
                <div className="grid grid-cols-4 gap-2">
                  {[20, 50, 100, 200].map((quickAmount) => (
                    <button
                      key={quickAmount}
                      type="button"
                      onClick={() => setAmount(quickAmount.toString())}
                      className="bg-gray-100 hover:bg-gray-200 text-gray-700 py-3 rounded-lg font-medium transition"
                    >
                      ${quickAmount}
                    </button>
                  ))}
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
                  className={`w-full py-5 rounded-xl font-bold text-xl transition disabled:opacity-50 disabled:cursor-not-allowed ${
                    transactionType === 'deposit'
                      ? 'bg-green-500 hover:bg-green-600 text-white'
                      : 'bg-orange-500 hover:bg-orange-600 text-white'
                  }`}
                >
                  {transactionLoading
                    ? 'Processing...'
                    : transactionType === 'deposit'
                    ? '💰 Deposit Funds'
                    : '💸 Withdraw Cash'}
                </button>
              </form>
            </>
          )}
        </div>

        {/* Footer */}
        <p className="text-center text-green-200 text-sm">
          For your security, please remember to exit when finished.
        </p>
      </div>
    </div>
  )
}
