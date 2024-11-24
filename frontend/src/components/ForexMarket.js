import React, { useState, useEffect } from "react";
import axios from "axios";
import {
    Box,
    TextField,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
 
    CircularProgress,
} from "@mui/material";
import ArrowDropUpIcon from "@mui/icons-material/ArrowDropUp";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import {axiosPrivate} from "../api/axios";

const ForexMarket = () => {
    const [forexData, setForexData] = useState([]);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [sortConfig, setSortConfig] = useState({ key: "bid", direction: "desc" });

    // OANDA API Base URL
    const OANDA_API_URL = "https://api-fxtrade.oanda.com/v3";
const  [error, setError] = useState([]);
    useEffect(() => {
        const fetchForexData = async () => {
            setLoading(true);
            try {
                const response = await axiosPrivate.get(`${OANDA_API_URL}/instruments`, {
                    headers: {
                        Authorization: `Bearer ${process.env.REACT_APP_OANDA_API_KEY}`,
                    },
                })
                if (response.status!==200){
                    setError(response)
                }

                const forexInstruments = response.data.instruments.filter((instrument) =>
                    instrument.type === "CURRENCY"
                );

                const forexQuotes = await Promise.all(
                    forexInstruments.map(async (instrument) => {
                        const quoteResponse = await axios.get(
                            `${OANDA_API_URL}/pricing?instruments=${instrument.name}`,
                            {
                                headers: {
                                    Authorization: `Bearer ${process.env.REACT_APP_OANDA_API_KEY}`,
                                },
                            }
                        );

                        return {
                            name: instrument.name,
                            displayName: instrument.displayName,
                            bid: quoteResponse.data.prices[0].bids[0].price,
                            ask: quoteResponse.data.prices[0].asks[0].price,
                            spread:
                                quoteResponse.data.prices[0].asks[0].price -
                                quoteResponse.data.prices[0].bids[0].price,
                        };
                    })
                );

                setForexData(forexQuotes);
            } catch (error) {
                console.error("Error fetching forex data:", error);
            }
            setLoading(false);
        };

        fetchForexData().catch(function (error) {
            console.error("Error fetching forex data:", error);
        })
    }, []);

    const handleSort = (key) => {
        const direction = sortConfig.key === key && sortConfig.direction === "asc" ? "desc" : "asc";
        setSortConfig({ key, direction });
        setForexData((prev) =>
            [...prev].sort((a, b) => {
                if (direction === "asc") {
                    return a[key] > b[key] ? 1 : -1;
                } else {
                    return a[key] < b[key] ? 1 : -1;
                }
            })
        );
    };

    const filteredForexData = forexData.filter((data) =>
        data.displayName.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <Box sx={{ padding: 4 }}>
            <Typography variant="h4" gutterBottom>
                Forex Market
            </Typography>
            <TextField
                label="Search Currency Pair"
                fullWidth
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                sx={{ marginBottom: 2 }}
            />
            {loading ? (
                <CircularProgress />
            ) : (
                <TableContainer component={Paper}>{error}
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>#</TableCell>
                                <TableCell>Name</TableCell>
                                <TableCell
                                    onClick={() => handleSort("bid")}
                                    style={{ cursor: "pointer" }}
                                >
                                    Bid
                                    {sortConfig.key === "bid" ? (
                                        sortConfig.direction === "asc" ? (
                                            <ArrowDropUpIcon />
                                        ) : (
                                            <ArrowDropDownIcon />
                                        )
                                    ) : null}
                                </TableCell>
                                <TableCell
                                    onClick={() => handleSort("ask")}
                                    style={{ cursor: "pointer" }}
                                >
                                    Ask
                                    {sortConfig.key === "ask" ? (
                                        sortConfig.direction === "asc" ? (
                                            <ArrowDropUpIcon />
                                        ) : (
                                            <ArrowDropDownIcon />
                                        )
                                    ) : null}
                                </TableCell>
                                <TableCell
                                    onClick={() => handleSort("spread")}
                                    style={{ cursor: "pointer" }}
                                >
                                    Spread
                                    {sortConfig.key === "spread" ? (
                                        sortConfig.direction === "asc" ? (
                                            <ArrowDropUpIcon />
                                        ) : (
                                            <ArrowDropDownIcon />
                                        )
                                    ) : null}
                                </TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredForexData.map((data, index) => (
                                <TableRow key={data.name}>
                                    <TableCell>{index + 1}</TableCell>
                                    <TableCell>{data.displayName}</TableCell>
                                    <TableCell>{data.bid}</TableCell>
                                    <TableCell>{data.ask}</TableCell>
                                    <TableCell>{data.spread.toFixed(5)}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </Box>
    );
};

export default ForexMarket;
