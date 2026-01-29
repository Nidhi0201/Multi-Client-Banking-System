'use client'

interface AccountCardProps {
  account: {
    accountNumber: number
    balance: number
    type: string
  }
  isSelected: boolean
  onClick: () => void
}

export default function AccountCard({ account, isSelected, onClick }: AccountCardProps) {
  return (
    <div
      onClick={onClick}
      className={`bg-white border-2 rounded-lg p-4 cursor-pointer transition ${
        isSelected
          ? 'border-primary-blue shadow-lg'
          : 'border-gray-200 hover:border-primary-blue hover:shadow'
      }`}
    >
      <div className="flex justify-between items-start mb-2">
        <div>
          <p className="text-sm text-gray-500">Account Number</p>
          <p className="text-lg font-bold text-gray-800">{account.accountNumber}</p>
        </div>
        <span className="bg-primary-blue text-white text-xs px-2 py-1 rounded">
          {account.type}
        </span>
      </div>
      <div className="mt-4">
        <p className="text-sm text-gray-500">Balance</p>
        <p className="text-2xl font-bold text-primary-blue">
          ${account.balance.toFixed(2)}
        </p>
      </div>
    </div>
  )
}
