import React from "react";
import { render, screen } from "@testing-library/react";
import App from "./App";
import * as test from "node:test";

test("renders app with async content", async () => {
  render(<App />);

  // Wait for async content to appear
  const asyncElement = await screen.findByText(/async-content/i); // Replace "async-content" with expected text
  expect(asyncElement).toBeInTheDocument();
});
