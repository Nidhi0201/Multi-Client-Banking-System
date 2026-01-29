'use client'

import { useState } from 'react'
import { accountsApi } from '@/lib/api'

interface AccountDetailsProps {
  account: {
    accountNumber: number
    balance: number
    type: string
    pin?: string
  }
  session: { sessionId: string; role: string; profile?: any }
}

export default function AccountDetails({ account, session }: AccountDetailsProps) {
  const [showPinUpdate, setShowPinUpdate] = useState(false)
  const [newPin, setNewPin] = useState('')
  const [pinMessage, setPinMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)
  const [loading, setLoading] = useState(false)

  const handleUpdatePin = async (e: React.FormEvent) => {
    e.preventDefault()
    if (newPin.length !== 4 || !/^\d+$/.test(newPin)) {
      setPinMessage({ type: 'error', text: 'PIN must be exactly 4 digits' })
      return
    }

    setLoading(true)
    setPinMessage(null)

    try {
      const response = await accountsApi.updatePin(account.accountNumber.toString(), newPin)
      if (response.success) {
        setPinMessage({ type: 'success', text: 'PIN updated successfully!' })
        setNewPin('')
        setShowPinUpdate(false)
      } else {
        setPinMessage({ type: 'error', text: response.error || 'Failed to update PIN' })
      }
    } catch (err: any) {
      setPinMessage({
        type: 'error',
        text: err.response?.data?.error || 'Failed to update PIN',
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-800 mb-4">Account Details</h2>
      
      <div className="space-y-4">
        <div className="flex justify-between items-center py-2 border-b">
          <span className="text-gray-600 font-medium">Account Number:</span>
          <span className="text-gray-800 font-bold">{account.accountNumber}</span>
        </div>
        
        <div className="flex justify-between items-center py-2 border-b">
          <span className="text-gray-600 font-medium">Account Type:</span>
          <span className="bg-primary-blue text-white text-xs px-3 py-1 rounded-full">
            {account.type}
          </span>
        </div>
        
        <div className="flex justify-between items-center py-2 border-b">
          <span className="text-gray-600 font-medium">Balance:</span>
          <span className="text-2xl font-bold text-primary-blue">
            ${account.balance.toFixed(2)}
          </span>
        </div>

        {session.role === 'employee' && (
          <div className="pt-4">
            {!showPinUpdate ? (
              <button
                onClick={() => setShowPinUpdate(true)}
                className="w-full bg-primary-blue hover:bg-blue-700 text-white py-2 px-4 rounded-lg font-medium transition"
              >
                Change PIN
              </button>
            ) : (
              <form onSubmit={handleUpdatePin} className="space-y-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    New PIN (4 digits)
                  </label>
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
                
                {pinMessage && (
                  <div
                    className={`px-4 py-2 rounded-lg ${
                      pinMessage.type === 'success'
                        ? 'bg-green-50 border border-green-200 text-green-700'
                        : 'bg-red-50 border border-red-200 text-red-700'
                    }`}
                  >
                    {pinMessage.text}
                  </div>
                )}
                
                <div className="flex gap-2">
                  <button
                    type="submit"
                    disabled={loading}
                    className="flex-1 bg-primary-blue hover:bg-blue-700 text-white py-2 px-4 rounded-lg font-medium transition disabled:opacity-50"
                  >
                    {loading ? 'Updating...' : 'Update PIN'}
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setShowPinUpdate(false)
                      setNewPin('')
                      setPinMessage(null)
                    }}
                    className="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-800 py-2 px-4 rounded-lg font-medium transition"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
