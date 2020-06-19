module.exports = {
  env: {
    node: true,
    es6: true,
    jasmine: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:@typescript-eslint/recommended-requiring-type-checking',
    'google',
  ],
  globals: {
    Atomics: 'readonly',
    SharedArrayBuffer: 'readonly',
    localStorage: true,
  },
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 11,
    sourceType: 'module',
    tsconfigRootDir: __dirname,
    project: 'tsconfig.eslint.json',
  },
  plugins: ['@typescript-eslint'],
  rules: {
    'require-jsdoc': 'off',
    'no-unused-vars': 'off',
    'eqeqeq': 'error',
    'new-cap': 'off',
    '@typescript-eslint/no-unused-vars': 'error',
    '@typescript-eslint/no-explicit-any': ['error', {fixToUnknown: true}],
    '@typescript-eslint/no-unnecessary-boolean-literal-compare': 'error',
    '@typescript-eslint/no-unnecessary-condition': 'error',
    '@typescript-eslint/prefer-readonly': 'error',
    '@typescript-eslint/promise-function-async': 'error',
    '@typescript-eslint/switch-exhaustiveness-check': 'error',

    // Handled by prettier.
    'semi': 'off',
    'linebreak-style': 'off',
    'max-len': 'off',
    'indent': 'off',
    'space-before-function-paren': 'off',
  },
  // Turn off rules that require type information for JS files.
  overrides: [
    {
      files: ['*.js'],
      rules: {
        '@typescript-eslint/explicit-function-return-type': 'off',
      },
    },
  ],
};
