import React, { Suspense, lazy, useState, useEffect } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { createRoot } from "react-dom/client";
import PropTypes from "prop-types";
import * as Sentry from '@sentry/react';

import { AuthProvider } from "./context/AuthProvider.js";
import LoadingSpinner from "./components/LoadingSpinner.js";
import "./index.css";


// Lazy-load main application component
const LazyApp = lazy(() => import("./App.js"));
// Replace with your actual DSN from Sentry
Sentry.init({
    dsn: "https://05f3db24103d140909fabe35afaab578@o4508501778169856.ingest.us.sentry.io/4508501779611648",
    integrations: [
        Sentry.browserTracingIntegration(),
        Sentry.replayIntegration(),
    ],
    // Tracing
    tracesSampleRate: 1.0, //  Capture 100% of the transactions
    // Set 'tracePropagationTargets' to control for which URLs distributed tracing should be enabled
    tracePropagationTargets: ["localhost", /^https:\/\/localhost:3000\/api/],
    // Session Replay
    replaysSessionSampleRate: 0.1, // This sets the sample rate at 10%. You may want to change it to 100% while in development and then sample at a lower rate in production.
    replaysOnErrorSampleRate: 1.0, // If you're not already sampling the entire session, change the sample rate to 100% when sampling sessions where errors occur.
});
const ModelCollapse = ({ isOpen, onToggle }) => {
    const [isExpanded, setIsExpanded] = useState(isOpen);

    useEffect(() => {
        setIsExpanded(isOpen);
    }, [isOpen]);

    return (
        <div className="model-collapse" onClick={onToggle}>
            {isExpanded ? "Expanded Content" : "Collapsed Content"}
        </div>
    );
};

ModelCollapse.defaultProps = {
    isOpen: false,
    onToggle: () => {},
};

ModelCollapse.propTypes = {
    isOpen: PropTypes.bool,
    onToggle: PropTypes.func,
};

const OperationContainer = ({ operations }) => {
    const [currentOperations, setCurrentOperations] = useState(operations);

    useEffect(() => {
        setCurrentOperations(operations);
    }, [operations]);

    return (
        <div className="operation-container">
            {currentOperations.map((operation, index) => (
                <div key={index}>{operation.name}</div>
            ))}
        </div>
    );
};

OperationContainer.defaultProps = {
    operations: [],
};

OperationContainer.propTypes = {
    operations: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string.isRequired,
        })
    ),
};
Sentry.addBreadcrumb({
    message: 'User clicked on submit button',
    category: 'ui',
    level: 'info',
});


createRoot(document.getElementById("root")).render(
    <Sentry.ErrorBoundary>
        <BrowserRouter>
            <AuthProvider>
                <Suspense fallback={<LoadingSpinner />}>
                    <Routes>
                        <Route path="/*" element={<LazyApp />} />
                    </Routes>
                </Suspense>
            </AuthProvider>
        </BrowserRouter>
    </Sentry.ErrorBoundary>
);
