'use client'

import { useState } from 'react'
import { accountsApi } from '@/lib/api'

interface CreateAccountFormProps {
  onAccountCreated: () => void
}

export default function CreateAccountForm({ onAccountCreated }: CreateAccountFormProps) {
  const [accountNumber, setAccountNumber] = useState('')
  const [pin, setPin] = useState('')
  const [accountType, setAccountType] = useState<'checking' | 'saving' | 'lineOfCredit'>('checking')
  const [initialBalance, setInitialBalance] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!accountNumber || !pin || !initialBalance) {
      setMessage({ type: 'error', text: 'Please fill in all fields' })
      return
    }

    if (accountNumber.length < 4) {
      setMessage({ type: 'error', text: 'Account number must be at least 4 digits' })
      return
    }

    if (pin.length !== 4 || !/^\d+$/.test(pin)) {
      setMessage({ type: 'error', text: 'PIN must be exactly 4 digits' })
      return
    }

    const balance = parseFloat(initialBalance)
    if (isNaN(balance) || balance < 0) {
      setMessage({ type: 'error', text: 'Initial balance must be a valid positive number' })
      return
    }

    setLoading(true)
    setMessage(null)

    try {
      const response = await accountsApi.createAccount({
        accountNumber,
        pin,
        type: accountType,
        initialBalance: balance,
      })

      if (response.success) {
        setMessage({ type: 'success', text: response.message || 'Account created successfully!' })
        setAccountNumber('')
        setPin('')
        setInitialBalance('')
        setAccountType('checking')
        setTimeout(() => {
          onAccountCreated()
        }, 1500)
      } else {
        setMessage({ type: 'error', text: response.error || 'Failed to create account' })
      }
    } catch (err: any) {
      setMessage({
        type: 'error',
        text: err.response?.data?.error || 'Failed to create account',
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="bg-gray-50 border-2 border-dashed border-gray-300 rounded-lg p-6">
      <h3 className="text-lg font-bold text-gray-800 mb-4">Create New Account</h3>
      
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Account Number
            </label>
            <input
              type="text"
              value={accountNumber}
              onChange={(e) => setAccountNumber(e.target.value.replace(/\D/g, ''))}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
              placeholder="e.g., 9999"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              PIN (4 digits)
            </label>
            <input
              type="text"
              maxLength={4}
              value={pin}
              onChange={(e) => setPin(e.target.value.replace(/\D/g, ''))}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
              placeholder="0000"
              required
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Account Type
          </label>
          <select
            value={accountType}
            onChange={(e) => setAccountType(e.target.value as any)}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
          >
            <option value="checking">Checking</option>
            <option value="saving">Saving</option>
            <option value="lineOfCredit">Line of Credit</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Initial Balance ($)
          </label>
          <input
            type="number"
            step="0.01"
            value={initialBalance}
            onChange={(e) => setInitialBalance(e.target.value)}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue focus:border-transparent"
            placeholder="0.00"
            required
          />
        </div>

        {message && (
          <div
            className={`px-4 py-3 rounded-lg ${
              message.type === 'success'
                ? 'bg-green-50 border border-green-200 text-green-700'
                : 'bg-red-50 border border-red-200 text-red-700'
            }`}
          >
            {message.text}
          </div>
        )}

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-green-500 hover:bg-green-600 text-white py-3 rounded-lg font-semibold transition disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? 'Creating...' : 'Create Account'}
        </button>
      </form>
    </div>
  )
}
