import React, { useEffect, useState } from 'react';
import { axiosPublic } from "../api/axios"; // XML to JSON converter

const StellarAccount = () => {
    const [account, setAccount] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchAccountData = async () => {
            try {
                const response = await axiosPublic.get("/stellar/accounts");
                setAccount(response.data); // Set the account data from the API response
            } catch (error) {
                setError(error.message); // Handle error in case of failure
            }
        };

        fetchAccountData().catch(error => {
            console.error("Error fetching account data: ", error)
        })

    }, [account,error]); // Empty dependency array ensures this only runs once

    if (error) {
        return <div>Error: {error}</div>; // Display an error message if fetching fails
    }

    if (!account) {
        return <div>Loading...</div>; // Show loading while data is being fetched
    }

    return (
        <div className="stellar-account">
            <h2>Stellar Account Details</h2>

            <table>
                <tbody>
                <tr>
                    <th>ID</th>
                    <td>{account.AccountResponse.id}</td>
                </tr>
                <tr>
                    <th>Account ID</th>
                    <td>{account.AccountResponse.accountId}</td>
                </tr>
                <tr>
                    <th>Sequence Number</th>
                    <td>{account.AccountResponse.sequenceNumber}</td>
                </tr>
                <tr>
                    <th>Last Modified Time</th>
                    <td>{account.AccountResponse.lastModifiedTime}</td>
                </tr>
                <tr>
                    <th>Thresholds</th>
                    <td>
                        Low: {account.AccountResponse.thresholds.lowThreshold},
                        Medium: {account.AccountResponse.thresholds.medThreshold},
                        High: {account.AccountResponse.thresholds.highThreshold}
                    </td>
                </tr>
                <tr>
                    <th>Flags</th>
                    <td>
                        Auth Required: {account.AccountResponse.flags.authRequired ? 'Yes' : 'No'},<br />
                        Auth Revocable: {account.AccountResponse.flags.authRevocable ? 'Yes' : 'No'}
                    </td>
                </tr>
                <tr>
                    <th>Balances</th>
                    <td>
                        {account.AccountResponse.balances && account.AccountResponse.balances.map((balance, index) => (
                            <div key={index}>
                                <p>Asset Code: {balance.assetCode}</p>
                                <p>Balance: {balance.balance}</p>
                            </div>
                        ))}
                    </td>
                </tr>
                </tbody>
            </table>

            <h3>Signers</h3>
            <ul>
                {account.AccountResponse.signers && account.AccountResponse.signers.map((signer, index) => (
                    <li key={index}>
                        <p>Key: {signer.key}</p>
                        <p>Type: {signer.type}</p>
                        <p>Public Key: {signer.publicKey}</p>
                    </li>
                ))}
            </ul>

            <h3>Links</h3>
            {account.AccountResponse.links && (
                <ul>
                    <li>
                        <a href={account.AccountResponse.links.self?.href}>Self</a>
                    </li>
                    <li>
                        <a href={account.AccountResponse.links.transactions?.href}>Transactions</a>
                    </li>
                    <li>
                        <a href={account.AccountResponse.links.operations?.href}>Operations</a>
                    </li>
                    <li>
                        <a href={account.AccountResponse.links.payments?.href}>Payments</a>
                    </li>
                    <li>
                        <a href={account.AccountResponse.links.effects?.href}>Effects</a>
                    </li>
                    <li>
                        <a href={account.AccountResponse.links.offers?.href}>Offers</a>
                    </li>
                    <li>
                        <a href={account.AccountResponse.links.trades?.href}>Trades</a>
                    </li>
                </ul>
            )}
        </div>
    );
};

export default StellarAccount;
