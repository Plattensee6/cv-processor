# Vite dev server in Docker (frontend)
FROM node:20-alpine

WORKDIR /app

# Only package.json first so this layer is cached when source changes
COPY package.json package-lock.json* ./

# Prefer ci (fast, deterministic) when lock exists; otherwise install (slower, run once then commit lock)
ENV NPM_CONFIG_LOGLEVEL=warn
ENV NPM_CONFIG_FETCH_TIMEOUT=120000
RUN npm ci --ignore-scripts 2>/dev/null || npm install --no-audit --no-fund

COPY . .

EXPOSE 5173

# Nodemon restarts dev server when config changes; Vite HMR handles source (with volume mount)
CMD ["sh", "-c", "npm run dev:watch"]
