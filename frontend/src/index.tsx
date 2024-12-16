import React, { StrictMode, Suspense, lazy} from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { createRoot } from "react-dom/client";

import { AuthProvider } from "./context/AuthProvider.js";
import LoadingSpinner from "./components/LoadingSpinner.js";
import ErrorBoundary from "./components/ErrorBoundary.js";
import "./index.css"

// Lazy-load main application component
const LazyApp = lazy(() => import("./App.js"));



createRoot(document.getElementById("root")).render(
    <StrictMode>
        <ErrorBoundary>
            <BrowserRouter>
                <AuthProvider>
                    <Suspense fallback={<LoadingSpinner />}>
                        <Routes>
                            <Route path="/*" element={<LazyApp />} />
                        </Routes>
                    </Suspense>
                </AuthProvider>
            </BrowserRouter>
        </ErrorBoundary>
    </StrictMode>
);