terraform {
  backend "gcs" {
    bucket = "terraform-state-bucket"
    prefix = "aipower/terraform.tfstate"
  }
}
