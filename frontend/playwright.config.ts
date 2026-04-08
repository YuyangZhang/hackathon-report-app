import { defineConfig, devices } from '@playwright/test';

const isCi = !!process.env['CI'];
const baseUrl = process.env['PLAYWRIGHT_BASE_URL'] || 'http://127.0.0.1:4200';

export default defineConfig({
  testDir: './tests/e2e',
  outputDir: 'test-results/playwright-artifacts',
  fullyParallel: true,
  retries: isCi ? 2 : 0,
  reporter: [
    ['list'],
    ['html', { outputFolder: 'test-results/playwright-report', open: 'never' }]
  ],
  use: {
    baseURL: baseUrl,
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'off'
  },
  webServer: [
    {
      command: './gradlew bootRun',
      cwd: '../backend',
      url: 'http://127.0.0.1:8080/api/test',
      reuseExistingServer: !isCi,
      timeout: 180000
    },
    {
      command: 'npm start -- --host 127.0.0.1 --port 4200',
      url: 'http://127.0.0.1:4200',
      reuseExistingServer: !isCi,
      timeout: 120000
    }
  ],
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] }
    }
  ]
});
