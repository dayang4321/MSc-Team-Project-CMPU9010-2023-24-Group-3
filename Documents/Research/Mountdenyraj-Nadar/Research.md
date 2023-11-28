# NEXT.JS Accessibility

## next.config.js Options

NEXT.JS can be configured through a next.config.js file in the root of your project directory (for example, by package.json).

>next.config.js

``` TypeScript
/** @type {import('next').NextConfig} */
const nextConfig = {
  /* config options here */
}

module.exports = nextConfig
```

`next.config.js` is a regular Node.js module, not a JSON file. It gets used by the NEXT.JS server and build phases, and it's not included in the browser build.

Reference: [NEXT.JS Accessibility Documentation](https://nextjs.org/docs/app/api-reference/next-config-js)

## NEXT.JS Guidelines for better web accessibility in our forms

Reference: [NEXT.JS Accessibility Article](https://nextjs.org/learn/dashboard-app/improving-accessibility)

# Data Science

## Llama 2 Development on AWS

Today, we focused on the deployment of Llama 2 on AWS. We explored various AWS services and how they can be utilized for efficient deployment and scaling. Key resources:

1. **SageMaker Notebook Example**: A comprehensive guide on deploying Llama 2 with AWS SageMaker.
   [View Notebook](https://llama-2-deployment-bhaw.notebook.eu-north-1.sagemaker.aws/examples/preview?example_id=%2Fhome%2Fec2-user%2Fsample-notebooks%2Fintroduction_to_amazon_algorithms%2Fjumpstart-foundation-models%2Fllama-2-text-completion.ipynb)
2. **Lambda Function Implementation**: Review of the Lambda function for Llama 2 deployment.
   [GitHub Repository](https://github.com/AIAnytime/Llama-2-Deployment-on-AWS/blob/bdec1d69738706c9a6c420cd05260fd486ab7663/lambda_function.py)

## Issues and Documentation in Llama 2 Deployment

Focused on resolving issues and enhancing documentation for Llama 2. Key highlights:

1. **Ollama2 Documentation**: Updated the API documentation for better clarity and usability.
   [API Documentation](https://github.com/jmorganca/ollama/blob/main/docs/api.md)
2. **Issues with Ollama2 on AWS EC2**: Investigated and documented solutions for running Ollama2 on AWS EC2.
   [GitHub Issue #630](https://github.com/jmorganca/ollama/issues/630)
   [GitHub Issue #788](https://github.com/jmorganca/ollama/issues/788)
