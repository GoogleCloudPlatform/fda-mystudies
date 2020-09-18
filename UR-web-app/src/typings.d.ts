declare let processEnv: ProcessEnv;

interface ProcessEnv {
    env: Env
}

interface Env {
    SERVER: string
    PEER: string
}

interface GlobalEnvironment{
    process: ProcessEnv;
}