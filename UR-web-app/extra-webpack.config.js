const webpack = require('webpack');

module.exports = {
    plugins: [new webpack.DefinePlugin({
        'process.env': {
            SERVER: JSON.stringify(processEnv.env.SERVER),
            PEER: JSON.stringify(processEnv.env.PEER)
        }
    })]
}