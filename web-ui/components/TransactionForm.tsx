'use client'

import { useState } from 'react'
import { accountsApi } from '@/lib/api'

interface TransactionFormProps {
  account: {
    accountNumber: number
    balance: number
    type: string
  }
  onTransaction: () => void
}

export default function TransactionForm({ account, onTransaction }: TransactionFormProps) {
  const [transactionType, setTransactionType] = useState<'deposit' | 'withdraw'>('deposit')
  const [amount, setAmount] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setMessage(null)

    try {
      const amountNum = parseFloat(amount)
      if (isNaN(amountNum) || amountNum <= 0) {
        setMessage({ type: 'error', text: 'Please enter a valid amount' })
        return
      }

      const response =
        transactionType === 'deposit'
          ? await accountsApi.deposit(account.accountNumber.toString(), amountNum)
          : await accountsApi.withdraw(account.accountNumber.toString(), amountNum)

      if (response.success) {
        setMessage({ type: 'success', text: response.message || 'Transaction successful!' })
        setAmount('')
        onTransaction()
      } else {
        setMessage({ type: 'error', text: response.error || 'Transaction failed' })
      }
    } catch (err: any) {
      setMessage({
        type: 'error',
        text: err.response?.data?.error || 'Transaction failed. Please try again.',
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-800 mb-4">Make Transaction</h2>

      <div className="flex gap-2 mb-4 bg-gray-100 rounded-lg p-1">
        <button
          onClick={() => setTransactionType('deposit')}
          className={`flex-1 py-2 px-4 rounded-md font-medium transition ${
            transactionType === 'deposit'
              ? 'bg-green-500 text-white shadow'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Deposit
        </button>
        <button
          onClick={() => setTransactionType('withdraw')}
          className={`flex-1 py-2 px-4 rounded-md font-medium transition ${
            transactionType === 'withdraw'
              ? 'bg-red-500 text-white shadow'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          Withdraw
        </button>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Amount ($)
          </label>
          <input
            type="number"
            step="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
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
          className={`w-full py-3 rounded-lg font-semibold transition disabled:opacity-50 disabled:cursor-not-allowed ${
            transactionType === 'deposit'
              ? 'bg-green-500 hover:bg-green-600 text-white'
              : 'bg-red-500 hover:bg-red-600 text-white'
          }`}
        >
          {loading ? 'Processing...' : transactionType === 'deposit' ? 'Deposit' : 'Withdraw'}
        </button>
      </form>
    </div>
  )
}
