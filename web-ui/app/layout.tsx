import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'Banking System',
  description: 'Modern Multi-Client Banking System',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  )
}
