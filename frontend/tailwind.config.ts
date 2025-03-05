import type { Config } from "tailwindcss";

export default {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      backgroundImage: {
        'black-purple-gradient': 'linear-gradient(90deg, rgba(33,33,33,1) 0%, rgba(101,10,176,1) 100%)',
        'black-green-gradient': 'linear-gradient(90deg, rgba(33,33,33,1) 0%, rgba(17,158,31,1) 100%)',
        'white-green-gradient': 'linear-gradient(90deg, rgba(132,213,121,1) 0%, rgba(26,119,23,1) 100%)',
        'instagram-gradient': 'linear-gradient(45deg, #F09433 0%, #E6683C 25%, #DC2743 50%, #CC2366 75%, #BC1888 100%);'
      },
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
        "line-color": "#25F712",
        "nav-buttons": "#B4DBB8",
        "green-hover": "#119E1F",
        "buttons-hover": "#10A51F",
        "middle-title": "#1A7717",
        "linkedin-bg" : "#0274B3",
        "youtube-bg" : "#FF0000",
        "input-border":"#3D6697",
        "border-forms":"#C4C4C4",
        "estadocuenta-border":"#E6E9EE",
        "letraestadodecuenta":"#AFAFAF",
        "transfer-color":"#435D6B",
        "transfer-inputs":"#93A300"
      },
    },
  },
  plugins: [],
} satisfies Config;
