// import { defineConfig, devices } from "@playwright/test";

// export default defineConfig({
//   testDir: "./tests",
//   timeout: 60_000,
//   expect: { timeout: 10_000 },
//   reporter: [["list"], ["html", { outputFolder: "playwright-report" }]],
//   use: {
//     baseURL: process.env.APP_BASE_URL ?? "http://localhost:5173",
//     trace: "on-first-retry",
//     video: "retain-on-failure",
//     actionTimeout: 15_000,
//   },
//   projects: [
//     { name: "chromium", use: { ...devices["Desktop Chrome"] } },
//     { name: "firefox", use: { ...devices["Desktop Firefox"] } },
//   ],
// });
