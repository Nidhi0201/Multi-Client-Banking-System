'use client'

interface LogsPanelProps {
  logs: string[]
}

export default function LogsPanel({ logs }: LogsPanelProps) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-800 mb-4">Activity Logs</h2>
      <div className="max-h-96 overflow-y-auto space-y-2">
        {logs.length === 0 ? (
          <p className="text-gray-500 text-sm">No logs available</p>
        ) : (
          logs.slice(-20).reverse().map((log, index) => {
            const parts = log.split(',')
            return (
              <div
                key={index}
                className="bg-gray-50 border border-gray-200 rounded p-2 text-xs font-mono"
              >
                <div className="text-gray-600">{parts[parts.length - 1]}</div>
                <div className="text-gray-800 mt-1">{parts.slice(0, -1).join(', ')}</div>
              </div>
            )
          })
        )}
      </div>
    </div>
  )
}
