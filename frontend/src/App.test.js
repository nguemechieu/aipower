import React from 'react';

import * as test from "node:test";
import App from "./App";
import {render} from "@testing-library/react";


test('renders learn react link', () => {
    render(<App/>

        );
    const linkElement = screen.getByText(/learn react/i);
    expect(linkElement).toBeInTheDocument();
});
