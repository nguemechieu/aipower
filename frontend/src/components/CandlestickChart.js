import React, { useEffect, useRef } from "react";
import { createChart } from "lightweight-charts";

const CandlestickChart = () => {
  const chartContainerRef = useRef(null);

  useEffect(() => {
    const chart = createChart(chartContainerRef.current, {
      width: chartContainerRef.current.clientWidth,
      height: 400,
      layout: {
        backgroundColor: "#6bd0e3",
        textColor: "#ffffff",
      },
      grid: {
        vertLines: {
          color: "#1d70cc",
        },
        horzLines: {
          color: "#1f917c",
        },
      },
      priceScale: {
        borderColor: "#45d16f",
      },
      timeScale: {
        borderColor: "#da8181",
      },
    });

    const candleSeries = chart.addCandlestickSeries();

    // Fetch mock data (replace with real-time API data)
    const fetchData = async () => {
      const mockData = [
        { time: "2024-11-15", open: 100, high: 110, low: 95, close: 105 },
        { time: "2024-11-16", open: 105, high: 115, low: 100, close: 110 },
        // Add more mock data...
      ];
      candleSeries.setData(mockData);
    };

    fetchData();

    // Resize listener
    const resizeHandler = () => {
      chart.resize(chartContainerRef.current.clientWidth, 400);
    };
    window.addEventListener("resize", resizeHandler);

    return () => {
      window.removeEventListener("resize", resizeHandler);
      chart.remove();
    };
  }, []);

  return <div ref={chartContainerRef} style={{ position: "relative" }} />;
};

export default CandlestickChart;
