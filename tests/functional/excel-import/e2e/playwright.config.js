import { defineConfig } from '@playwright/test'

export default defineConfig({
  testDir: '.',
  testMatch: '**/*.spec.js',
  timeout: 60_000,
  expect: { timeout: 15_000 },
  workers: 1,
  reporter: 'list',
  use: {
    baseURL: process.env.APP_BASE_URL || 'http://frontend',
    browserName: 'chromium',
    headless: true,
    trace: 'retain-on-failure',
  },
})
