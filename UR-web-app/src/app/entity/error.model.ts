export interface ApiResponse {
  error: Error;
  userMessage: string;
}
export interface Error {
  detailMessage: string;
  type: string;
  userMessage: string;
}
