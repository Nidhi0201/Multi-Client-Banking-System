import axios from 'axios'

const API_BASE = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add auth token to requests
api.interceptors.request.use((config) => {
  const session = localStorage.getItem('bankingSession')
  if (session) {
    const { sessionId } = JSON.parse(session)
    config.headers.Authorization = `Bearer ${sessionId}`
  }
  return config
})

export const authApi = {
  employeeLogin: async (username: string, password: string) => {
    const response = await api.post('/auth/employee-login', { username, password })
    return response.data
  },
  customerLogin: async (username: string, password: string) => {
    const response = await api.post('/auth/customer-login', { username, password })
    return response.data
  },
  atmLogin: async (accountNumber: string, pin: string) => {
    const response = await api.post('/auth/atm-login', { accountNumber, pin })
    return response.data
  },
  logout: async () => {
    await api.post('/auth/logout')
  },
}

export const accountsApi = {
  getAccounts: async () => {
    const response = await api.get('/accounts')
    return response.data
  },
  getBalance: async (accountNumber: string) => {
    const response = await api.get(`/accounts/balance?accountNumber=${accountNumber}`)
    return response.data
  },
  deposit: async (accountNumber: string, amount: number) => {
    const response = await api.post('/accounts/deposit', { accountNumber, amount })
    return response.data
  },
  withdraw: async (accountNumber: string, amount: number) => {
    const response = await api.post('/accounts/withdraw', { accountNumber, amount })
    return response.data
  },
  updatePin: async (accountNumber: string, pin: string) => {
    const response = await api.post('/accounts/update-pin', { accountNumber, pin })
    return response.data
  },
  createAccount: async (accountData: {
    accountNumber: string
    pin: string
    type: string
    initialBalance: number
  }) => {
    const response = await api.post('/accounts/create', accountData)
    return response.data
  },
  searchAccount: async (accountNumber: string) => {
    const response = await api.get(`/accounts/search?accountNumber=${accountNumber}`)
    return response.data
  },
  linkAccountToProfile: async (accountNumber: string, username: string) => {
    const response = await api.post('/accounts/link', { accountNumber, username })
    return response.data
  },
}

export const profilesApi = {
  createProfile: async (profileData: {
    name: string
    username: string
    password: string
    phone: number
    address: string
    email: string
  }) => {
    const response = await api.post('/profiles/create', profileData)
    return response.data
  },
  searchProfile: async (username: string) => {
    const response = await api.get(`/profiles/search?username=${username}`)
    return response.data
  },
  updateProfile: async (profileData: {
    username: string
    name?: string
    password?: string
    phone?: string
    address?: string
    email?: string
    creditScore?: string
  }) => {
    const response = await api.post('/profiles/update', profileData)
    return response.data
  },
}

export const logsApi = {
  getLogs: async () => {
    const response = await api.get('/logs')
    return response.data
  },
}
