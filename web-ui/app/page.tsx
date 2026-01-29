'use client'

import { useState, useEffect } from 'react'
import LoginPage from '@/components/LoginPage'
import Dashboard from '@/components/Dashboard'

export default function Home() {
  const [session, setSession] = useState<{ sessionId: string; role: string; profile?: any; account?: any } | null>(null)

  useEffect(() => {
    // Check for existing session in localStorage
    const savedSession = localStorage.getItem('bankingSession')
    if (savedSession) {
      try {
        setSession(JSON.parse(savedSession))
      } catch (e) {
        localStorage.removeItem('bankingSession')
      }
    }
  }, [])

  const handleLogin = (sessionData: { sessionId: string; role: string; profile?: any; account?: any }) => {
    setSession(sessionData)
    localStorage.setItem('bankingSession', JSON.stringify(sessionData))
  }

  const handleLogout = () => {
    setSession(null)
    localStorage.removeItem('bankingSession')
  }

  if (!session) {
    return <LoginPage onLogin={handleLogin} />
  }

  return <Dashboard session={session} onLogout={handleLogout} />
}
