FROM mcr.microsoft.com/playwright:v1.55.0-noble

WORKDIR /workspace
COPY package.json package-lock.json ./
COPY node_modules ./node_modules
COPY . ./
CMD ["node", "node_modules/@playwright/test/cli.js", "test"]
