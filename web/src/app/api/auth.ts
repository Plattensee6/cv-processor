import { getApiUrl } from './client';

export type LoginResponse = { accessToken: string; tokenType: string };
export type MessageResponse = { message: string };
export type ForgotPasswordResponse = { message: string; resetToken: string | null };
export type ErrorResponse = { message: string };

export async function login(email: string, password: string): Promise<LoginResponse> {
  const url = getApiUrl('/auth/login');
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error((data as ErrorResponse).message || 'Login failed');
  }
  return data as LoginResponse;
}

export async function register(email: string, fullName: string, password: string): Promise<MessageResponse> {
  const url = getApiUrl('/auth/register');
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, fullName, password }),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error((data as ErrorResponse).message || 'Registration failed');
  }
  return data as MessageResponse;
}

export async function forgotPassword(email: string): Promise<ForgotPasswordResponse> {
  const url = getApiUrl('/auth/forgot-password');
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error((data as ErrorResponse).message || 'Request failed');
  }
  return data as ForgotPasswordResponse;
}

export async function resetPassword(token: string, newPassword: string): Promise<MessageResponse> {
  const url = getApiUrl('/auth/reset-password');
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token, newPassword }),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error((data as ErrorResponse).message || 'Reset failed');
  }
  return data as MessageResponse;
}
