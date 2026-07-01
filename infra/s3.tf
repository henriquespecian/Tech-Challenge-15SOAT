resource "aws_s3_bucket" "bucket" {
  bucket = "my-tf-test-bucket-henriquespecian"

  tags = var.tags
}