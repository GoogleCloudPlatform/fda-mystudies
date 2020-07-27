// node . [token_count]
// args:
//   token_count: number of tokens to generate

const validChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
const validCharsLength = validChars.length;
const tokenLength = 6;
const defaultTokenCount = 10;

const myArgs = process.argv.slice(2);
const maybeInt = parseInt(myArgs[0]);
const tokenCount = Number.isInteger(maybeInt) ? maybeInt : defaultTokenCount;

const randomToken = (length) => {
  return [...Array(length)]
    .map(() => validChars.charAt(Math.floor(Math.random() * validCharsLength)))
    .join("");
};

console.log(
  [...Array(tokenCount)].map(() => randomToken(tokenLength)).join("\n")
);
