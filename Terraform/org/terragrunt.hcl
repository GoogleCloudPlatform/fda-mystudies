remote_state {
  backend = "gcs"
  config = {
    bucket = "heroes-hat-dev-terraform-state-08679"
    prefix = "${path_relative_to_include()}"
  }
}
