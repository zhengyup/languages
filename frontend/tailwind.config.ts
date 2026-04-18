import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{ts,tsx}",
    "./components/**/*.{ts,tsx}",
    "./lib/**/*.{ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        ink: "#182235",
        muted: "#667085",
        border: "#dbe4f0",
        accent: "#3465eb",
        surface: "#ffffff",
        page: "#f5f7fb",
        soft: "#eef4ff"
      },
      boxShadow: {
        card: "0 12px 32px rgba(24, 34, 53, 0.06)"
      },
      borderRadius: {
        card: "24px"
      },
      fontFamily: {
        sans: ["Avenir Next", "Segoe UI", "Helvetica Neue", "sans-serif"],
        serif: ["Iowan Old Style", "Palatino Linotype", "Book Antiqua", "serif"]
      }
    }
  },
  plugins: []
};

export default config;

