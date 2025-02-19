import React, { Suspense, lazy } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import * as Sentry from "@sentry/react";
import { AuthProvider } from "./context/AuthProvider";
import LoadingSpinner from "./components/LoadingSpinner";
import "./index.css";
import ErrorBoundary from "./components/ErrorBoundary";
import {createRoot} from "react-dom/client";

// Lazy-load the main application component for better performance
const LazyApp = lazy(() => import("./App"));

// Sentry Initialization for error monitoring and performance tracking
Sentry.init({
  dsn: "https://05f3db24103d140909fabe35afaab578@o4508501778169856.ingest.us.sentry.io/4508501779611648", // Replace with your actual DSN
  integrations: [
    Sentry.browserTracingIntegration(), // Enables browser performance tracing
    Sentry.replayIntegration(), // Allows session replay feature
  ],
  tracesSampleRate: 1.0, // Capture 100% of the transactions for performance monitoring
  tracePropagationTargets: ["localhost", /^https:\/\/localhost:3000\/api/], // Include local API in traces
  replaysSessionSampleRate: 0.1, // Sample rate for session replays (10%)
  replaysOnErrorSampleRate: 1.0, // Capture all sessions when an error occurs
});

// Add a breadcrumb to Sentry for tracking UI events
Sentry.addBreadcrumb({
  message: "User clicked on submit button",
  category: "ui",
  level: "info",
});

// Get the root element to render the app
const rootElement = document.getElementById("root");

if (rootElement) {
  createRoot(rootElement).render(
    <Sentry.ErrorBoundary fallback={<LoadingSpinner />}>
      {/* ErrorBoundary to catch errors in the application */}

        <BrowserRouter>
          <ErrorBoundary>
          {/* AuthProvider to manage authentication state */}
          <AuthProvider>
            {/* Suspense for lazy loading with a fallback UI */}
            <Suspense fallback={<LoadingSpinner />}>
              <Routes>
                {/* Define the route for the lazy-loaded App component */}
                <Route path="/*" element={<LazyApp />} />
              </Routes>
            </Suspense>
          </AuthProvider>
          </ErrorBoundary>
        </BrowserRouter>

    </Sentry.ErrorBoundary>,
  );
}
