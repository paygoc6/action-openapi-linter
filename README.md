# PayGo OpenAPI linter

This is a composite run steps action to simplify a call to the OpenAPI linter created by PayGo.

It runs the Docker image from a private container registry to validate an OpenAPI file.

Although this is a public repository, the validation project is a property of PayGo. 

## Usage

This action can be run on `ubuntu-latest` or linux based runners provided by PayGo.

This is a minimal step to use `paygoc6/openapi-linter`:

```
- uses: paygoc6/openapi-linter@v1
  with:
    registry: my_registry
```

You must login to the Docker registry beforehand to be able to use this action.

## Inputs

The action supports the following inputs:

- `registry` - (required) The registry URL to pull the image.
- `path-in` - (optional) Path where the OpenAPI file will be available on runtime. Default is `target/swagger`.
- `file-name` - (optional) Name of the OpenAPI file. Default is `swagger.json`.

## Outputs

By now, no outputs are set for this action.