export interface ApiResponse {
  error: Error;
}
export interface Error {
  detailMessage: string;
  type: string;
  userMessage: string;
}
