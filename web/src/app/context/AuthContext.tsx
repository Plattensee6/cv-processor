import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import * as authApi from '../api/auth';
import { decodeJwtPayload, isTokenExpired } from '../lib/jwt';

const STORAGE_KEY = 'hr_access_token';

export type AuthUser = {
  email: string;
  fullName: string;
};

type AuthContextValue = {
  token: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  register: (email: string, fullName: string, password: string) => Promise<void>;
  forgotPassword: (email: string) => Promise<{ resetToken: string | null }>;
  resetPassword: (token: string, newPassword: string) => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState<string | null>(() => {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(STORAGE_KEY);
  });
  const [isLoading, setIsLoading] = useState(true);

  const setToken = useCallback((value: string | null) => {
    setTokenState(value);
    if (typeof window !== 'undefined') {
      if (value) localStorage.setItem(STORAGE_KEY, value);
      else localStorage.removeItem(STORAGE_KEY);
    }
  }, []);

  const user = useMemo((): AuthUser | null => {
    if (!token || isTokenExpired(token)) return null;
    const payload = decodeJwtPayload(token);
    if (!payload?.sub) return null;
    return {
      email: payload.sub,
      fullName: payload.fullName ?? payload.sub,
    };
  }, [token]);

  useEffect(() => {
    if (!token) {
      setIsLoading(false);
      return;
    }
    if (isTokenExpired(token)) {
      setToken(null);
    }
    setIsLoading(false);
  }, [token, setToken]);

  const login = useCallback(
    async (email: string, password: string) => {
      const res = await authApi.login(email, password);
      setToken(res.accessToken);
    },
    [setToken]
  );

  const logout = useCallback(() => {
    setToken(null);
  }, [setToken]);

  const register = useCallback(async (email: string, fullName: string, password: string) => {
    await authApi.register(email, fullName, password);
  }, []);

  const forgotPassword = useCallback(async (email: string) => {
    const res = await authApi.forgotPassword(email);
    return { resetToken: res.resetToken ?? null };
  }, []);

  const resetPassword = useCallback(async (token: string, newPassword: string) => {
    await authApi.resetPassword(token, newPassword);
  }, []);

  const value: AuthContextValue = useMemo(
    () => ({
      token,
      user,
      isAuthenticated: !!user,
      isLoading,
      login,
      logout,
      register,
      forgotPassword,
      resetPassword,
    }),
    [token, user, isLoading, login, logout, register, forgotPassword, resetPassword]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
