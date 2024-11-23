import React, { StrictMode, Suspense, lazy, Component } from "react";

import { BrowserRouter, Routes, Route } from "react-router-dom";
import { createRoot } from "react-dom/client";

import { AuthProvider } from "./context/AuthProvider.js";
import LoadingSpinner from "./components/LoadingSpinner.js";
import {Button} from "@mui/material";

// Lazy-load main application component
const LazyApp = lazy(() => import("./App.js"));

class ErrorBoundary extends Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, error: '' };
    }

    static getDerivedStateFromError(error) {
        return { hasError: true, error };
    }

    componentDidCatch(error, errorInfo) {
        console.error("Error caught in ErrorBoundary:", error, errorInfo);
    }


    render() {
        if (this.state.hasError) {
            return (
                <div className="info">
                    <p>Oops! Something went wrong.</p>
                    <pre>{this.state.error && this.state.error.toString()}</pre>
                    <Button onClick={this.resetError}>Try again</Button>
                </div>
            );
        }

        return this.props.children;
    }
}

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
