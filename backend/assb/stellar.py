import numpy as np
from flask import Flask, request, jsonify
from flask_cors import CORS
from keras.src.saving import load_model
from tensorflow.python.keras import Sequential, Input
from tensorflow.python.keras.layers import Dense

from assb import calculate_sentiment

app = Flask(__name__)

# Model setup
saved_model_dir = "saved_model/saved_model.keras"
try:
    model = load_model(saved_model_dir)  # Load the model if already saved
except:
    # Define and save the model if not found
    model = Sequential([
        Input(shape=(10,)),  # Input layer with 10 features
        Dense(32, activation='relu'),  # Hidden layer
        Dense(1, activation='sigmoid')  # Output layer for binary classification
    ])
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
    model.save(saved_model_dir)

print(f"Model ready at: {saved_model_dir}")

# Endpoint to fetch predictions
@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Get data from the request
        data = request.json
        features = np.array(data['features']).reshape(1, 10)  # Match the model input shape

        # Make predictions
        prediction = model.predict(features)
        signal = "BUY" if prediction[0][0] > 0.5 else "SELL" if prediction[0][0] < 0.5 else "HOLD"
        return jsonify({"signal": signal, "score": float(prediction[0][0])})
    except ValueError as e:
        return jsonify({"error": "Invalid input", "details": str(e)}), 400
    except Exception as e:
        return jsonify({"error": "Internal server error", "details": str(e)}), 500

# Endpoint to calculate sentiment
@app.route('/sentiment', methods=['GET'])
def sentiment():
    try:
        symbol ="BTC/USD" #request.args.get("symbol")  # Use query parameters for GET
        if not symbol:
            return jsonify({"error": "Missing 'symbol' parameter"}), 400
        score = calculate_sentiment(symbol)
        return jsonify({"sentiment_score": score})
    except Exception as e:
        return jsonify({"error": "Internal server error", "details": str(e)}), 500

if __name__ == "__main__":
    app.run(host="localhost", port=5000)
    CORS(app)
