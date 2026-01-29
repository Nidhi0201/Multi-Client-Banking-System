'use client'

import { authApi } from '@/lib/api'
import EmployeeDashboard from './EmployeeDashboard'
import CustomerDashboard from './CustomerDashboard'
import ATMDashboard from './ATMDashboard'

interface DashboardProps {
  session: { sessionId: string; role: string; profile?: any; account?: any }
  onLogout: () => void
}

export default function Dashboard({ session, onLogout }: DashboardProps) {
  const handleLogout = async () => {
    try {
      await authApi.logout()
    } catch (err) {
      console.error('Logout error:', err)
    }
    onLogout()
  }

  // Route to appropriate dashboard based on role
  if (session.role === 'employee') {
    return <EmployeeDashboard session={session} onLogout={handleLogout} />
  }

  if (session.role === 'atm') {
    return <ATMDashboard session={session} onLogout={handleLogout} />
  }

  return <CustomerDashboard session={session} onLogout={handleLogout} />
}
