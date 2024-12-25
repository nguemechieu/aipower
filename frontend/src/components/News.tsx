import React, { useEffect, useState } from "react";
import axios from "axios";
import LoadingSpinner from "./LoadingSpinner";


const NewsDisplay = () => {
  const [newsList, setNewsList] = useState([
    // Sample data for demonstration purposes
    {
      id: 1,
      title: "AI Power's Innovative Solutions",
      date: "2022-02-14T12:00:00",
      content:
        "AI Power is introducing its latest innovative solutions for businesses. Check out our blog post for more details.",
    },
  ]); // State to handle all news items
  const [error, setError] = useState(null); // State to handle error messages
  const [upcomingNews, setUpcomingNews] = useState([]); // State to handle only upcoming events

  const fetchNewsData = async () => {
    try {
      const response = await axios.get("/api/v3/news");
      if (response.status !== 200) {
        setError(`HTTP error! status: ${response.data}`);
      }
      const currentNews = response.data.filter(
        (news: { date: string | number | Date; }) => new Date(news.date) <= new Date(),
      );
      const futureNews = response.data.filter(
        (news: { date: string | number | Date; }) => new Date(news.date) > new Date(),
      );

      setNewsList(currentNews); // Set data for past/current events
      setUpcomingNews(futureNews); // Set data for upcoming events
    } catch (error) {
      console.error("Error fetching news " + error.message);
    }
  };

  useEffect(() => {
    fetchNewsData().then(() => {
      console.log("News data fetched successfully");
    });
  }, []);

  return error ? (
    <div style={styles.error}>
      <h1>News Error</h1>
      <p>{error}</p>
    </div>
  ) : (
    <div className={"container"} style={styles.container}>
      <h1 style={styles.title}>AI Power News</h1>

      <h2 style={styles.title}>Upcoming News</h2>
      {upcomingNews.length > 0 ? (
        upcomingNews.map((news, index) => (
          <div key={index} style={styles.newsItem}>
            <div style={styles.detail}>
              <strong>Title:</strong> {news.title || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Country:</strong> {news.country || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Date:</strong>{" "}
              {new Date(news.date).toLocaleString() || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Impact:</strong> {news.impact || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Forecast:</strong> {news.forecast || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Previous:</strong> {news.previous || "N/A"}
            </div>
          </div>
        ))
      ) : (
        <p style={styles.loading}>No upcoming events</p>
      )}

      <h2 style={styles.title}>News Summary</h2>
      {newsList.length > 0 ? (
        newsList.map((news, index) => (
          <div key={index} style={styles.newsItem}>
            <div style={styles.detail}>
              <strong>Title:</strong> {news.title || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Country:</strong> {news.country || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Date:</strong>{" "}
              {new Date(news.date).toLocaleString() || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Impact:</strong> {news.impact || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Forecast:</strong> {news.forecast || "N/A"}
            </div>
            <div style={styles.detail}>
              <strong>Previous:</strong> {news.previous || "N/A"}
            </div>
          </div>
        ))
      ) : (
        <p><LoadingSpinner/></p>
      )}
    </div>
  );
};

// Default styles for the component
const styles = {
  container: {
    border: "1px solid #ccc",
    borderRadius: "8px",
    padding: "20px",
    maxWidth: "600px",
    fontFamily: "Arial, sans-serif",
    backgroundColor: "#0a3936",
    boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
  },
  title: {
    color: "#1d70cc",
    textAlign: "center",
    marginBottom: "15px",
  },
  newsItem: {
    borderBottom: "1px solid #ddd",
    paddingBottom: "15px",
    marginBottom: "15px",
  },
  detail: {
    marginBottom: "10px",
    color: "#caefd5",
  },
  loading: {
    color: "#45d16f",
    textAlign: "center",
  },
  link: {
    color: "#45d16f",
  },
};

export default NewsDisplay;
