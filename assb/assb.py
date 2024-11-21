import os
import time
import ccxt
import pandas as pd
import numpy as np
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
from dotenv import load_dotenv
import requests

# Load environment variables from .env file
load_dotenv()

# Initialize sentiment analyzer
sia = SentimentIntensityAnalyzer()

# Initialize exchange
exchange = ccxt.binanceus({
    "rateLimit": 1200,
    "enableRateLimit": True,
})


def fetch_market_data(pair, timeframe="1h", limit=50):
    """
    Fetch historical OHLCV data using ccxt.
    """
    try:


        exchange.options["recvWindow"] = 5000  # Set the receive window to 5 seconds for better performance
        exchange.options["defaultType"] = "spot"  # Set the default type to spot for better performance
        exchange.load_markets()  # Load all markets for efficient data fetching
        exchange.verbose = False  # Set verbose mode to False for better performance
        exchange.timeout = 30000  # Set timeout to 30 seconds

        print(f"Fetching market data for {pair} on timeframe {timeframe}...")
        ohlcv = exchange.fetch_ohlcv(pair, timeframe=timeframe, limit=limit)
        data = pd.DataFrame(ohlcv, columns=["timestamp", "open", "high", "low", "close", "volume"])
        data["timestamp"] = pd.to_datetime(data["timestamp"], unit="ms")  # Convert timestamp to datetime
        return data
    except Exception as e:
        print(f"Error fetching market data: {e}")
        return pd.DataFrame()


def calculate_sentiment(symb):
    """
    Analyze sentiment score using NewsAPI and VADER.
    """
    news_api_key = os.getenv("NEWS_API_KEY")
    if not news_api_key:
        print("News API key not found in environment variables.")
        return 0

    params = {"q": symb, "apiKey": news_api_key}
    try:
        response = requests.get("https://newsapi.org/v2/everything", params=params)
        articles = response.json().get("articles", [])
        news_data = [article["title"] for article in articles]
        print(f"Fetched {len(news_data)} news articles.")
        print({news_data.__str__()})

        # Calculate sentiment using VADER
        scores = [sia.polarity_scores(headline)["compound"] for headline in news_data]
        return np.mean(scores) if scores else 0
    except Exception as e:
        print(f"Error fetching or analyzing news data: {e}")
        return 0


def get_technical_indicators(data):
    """
    Calculate MACD, RSI, and Bollinger Bands.
    """
    try:
        # MACD
        data["EMA12"] = data["close"].ewm(span=12).mean()
        data["EMA26"] = data["close"].ewm(span=26).mean()
        data["MACD"] = data["EMA12"] - data["EMA26"]

        # RSI
        delta = data["close"].diff()
        gain = np.where(delta > 0, delta, 0)
        loss = np.where(delta < 0, abs(delta), 0)
        avg_gain = pd.Series(gain).rolling(window=14).mean()
        avg_loss = pd.Series(loss).rolling(window=14).mean()
        data["RSI"] = 100 - (100 / (1 + (avg_gain / avg_loss)))

        # Bollinger Bands
        data["SMA20"] = data["close"].rolling(window=20).mean()
        data["stddev"] = data["close"].rolling(window=20).std()
        data["UpperBB"] = data["SMA20"] + (2 * data["stddev"])
        data["LowerBB"] = data["SMA20"] - (2 * data["stddev"])

        # Drop rows with NaN values
        data.dropna(inplace=True)
        return data
    except Exception as e:
        print(f"Error calculating technical indicators: {e}")
        return data


def decide_trade(data, sentiment_score):
    """
    Generate trading signals based on sentiment and indicators.
    """
    latest = data.iloc[-1]

    # Buy Signal
    if latest["RSI"] < 30 and latest["close"] < latest["LowerBB"] and sentiment_score > 0.7:
        return "BUY"

    # Sell Signal
    if latest["RSI"] > 70 and latest["close"] > latest["UpperBB"] and sentiment_score < -0.7:
        return "SELL"

    return "HOLD"


def place_trade(pair, action, amount):
    """
    Mock trade execution.
    Replace it with actual API logic to execute trades.
    """
    try:
        print(f"Trade executed: {action} {amount} of {pair}")
    except Exception as e:
        print(f"Error placing trade: {e}")


def assb_bot():
    """
    Main function to execute the trading bot logic.
    """
    try:
        # Define symbol and amount to trade

        symbols=[
            "BTC/USD",
            "ETH/USD",
            "LTC/USD",
            "ADA/USD",
            "BCH/USD",
            "BNB/USD",
            "XLM/USD",
            "DOGE/USD",
            "LINK/USD",
            "DASH/USD"
        ]

        amount = 0.01
        for symb in symbols:
         # Load API credentials from environment variables
          exchange.apiKey = os.getenv("BINANCE_API_KEY")
          exchange.secret = os.getenv("BINANCE_API_SECRET_KEY")

        # Check if API credentials are available

          if not exchange.apiKey or not exchange.secret:
            print("API credentials not found. Skipping this iteration.")
            return

        # Fetch market data
          market_data = fetch_market_data(symb)
          print("Market data", market_data.__str__())
          if market_data.empty:
            print("No market data available. Skipping this iteration.")
            return

        # Calculate sentiment
          sentiment_score = calculate_sentiment(symb)

         # Calculate technical indicators
          enriched_data = get_technical_indicators(market_data)

         # Decide trade
          decision = decide_trade(enriched_data, sentiment_score)

         # Execute trade
          if decision == "BUY":
            place_trade(symb, "BUY", amount=amount)
          elif decision == "SELL":
            place_trade(symb, "SELL", amount=amount)
          else:
            print("No trade signal.")
    except Exception as e:
            print(f"Error in bot execution: {e}")


if __name__ == "__main__":
    while True:
        assb_bot()
        time.sleep(60)  # Wait for 1 minute before the next iteration
