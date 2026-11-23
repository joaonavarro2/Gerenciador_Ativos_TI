FROM mcr.microsoft.com/playwright:v1.55.0-noble

WORKDIR /workspace
COPY tests/functional/excel-import/e2e/package.json tests/functional/excel-import/e2e/package-lock.json ./tests/functional/excel-import/e2e/
RUN npm ci --prefix tests/functional/excel-import/e2e
COPY tests/functional/excel-import/e2e/ ./tests/functional/excel-import/e2e/
CMD ["npm", "test", "--prefix", "tests/functional/excel-import/e2e"]
