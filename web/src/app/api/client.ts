const getBaseUrl = () => {
  const url = import.meta.env.VITE_API_URL;
  if (url) return url.replace(/\/$/, '');
  return ''; // same origin, use relative /api
};

export function getApiUrl(path: string): string {
  const base = getBaseUrl();
  const p = path.startsWith('/') ? path : `/${path}`;
  return `${base}/api${p}`;
}

export async function fetchApi<T>(
  path: string,
  options: RequestInit & { token?: string | null } = {}
): Promise<T> {
  const { token, ...init } = options;
  const url = getApiUrl(path);
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(init.headers as Record<string, string>),
  };
  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
  }
  const res = await fetch(url, { ...init, headers });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error((data as { message?: string }).message || res.statusText || 'Request failed');
  }
  return data as T;
}
