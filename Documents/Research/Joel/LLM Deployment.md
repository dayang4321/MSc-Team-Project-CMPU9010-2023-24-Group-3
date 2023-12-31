Tries deployment in the free tier instance.
Error: The Llama runner has been terminated. This error is caused by the free tier not having enough RAM to run the runner when the API is hit. The free tier has only 1 GB of RAM.

Deployment on paid instances of EC2. (t.xlarge and t.2xlarge)
When we tried running on paid instances such as t.xlarge and t.2xlarge we were able to hit the API and successfully get the results. However, the issue we face is that the response takes around 15-30 mins. This is quite a long wait for the user and also due to the single-threaded approach of the document processing, this can cause a backlog and might cause the system to crash.

We have asked Amazon for access to ec2 instances with GPU but we haven't received any response yet.

Deployment and connecting via Local computer.
To make your local PC, where you have a service (like Ollama) running on port 11434, accessible over the internet from another PC, you need to follow several steps:

Static IP or Dynamic DNS: Ensure your PC has a static IP address within your local network. If your public IP is dynamic (changes frequently), you might need a Dynamic DNS service so that you can access your PC with a domain name that updates its IP address automatically.

Configure Port Forwarding: You need to set up port forwarding on your router. This process varies depending on the router's make and model, but generally involves:

Logging into your router's configuration page (usually through a web browser).
Navigating to the port forwarding section.
Setting up a new port forwarding rule to forward traffic from the external port 11434 to the internal IP address of your PC (where Ollama is running) and the same port number (11434).
Firewall Configuration: Ensure your PC’s firewall allows incoming connections on port 11434. You might need to create a rule in your firewall settings to allow this.
Testing: Once you have configured port forwarding and firewall settings, you can test the setup. Use another device outside your local network and try to connect to your service using your public IP address (or Dynamic DNS hostname) followed by the port number (e.g., http://[YourPublicIP]:11434 or http://[YourDynamicDNSHostname]:11434).
Security Considerations: Exposing a local service to the internet comes with security risks. Ensure Ollama is secured and updated. Consider implementing additional security measures such as VPNs, SSL/TLS, or access control mechanisms.
Check ISP Policy and Router Capabilities: Some Internet Service Providers (ISPs) may block incoming connections on certain ports, and not all routers support port forwarding. Verify these aspects if you encounter issues.
Since Ollama is running on WSL,  WSL2 noticeably does not share an IP address with your computer. Because WSL2 was implemented with Hyper-V, it runs with a virtualized ethernet adapter. Your computer hides WSL2 behind a NAT where WSL2 has its unique IP. Although WSL2 can be accessed on your machine through localhost thanks to recent updates, these ports will not be open on your LAN.
https://jwstanly.com/blog/article/Port+Forwarding+WSL+2+to+Your+LAN/

Development and coding of the crude solutions in case we are not successful in deploying the LLM’s.

Faced a lot of changes as I had to research everything. As both WSL2 and ur computer dot have the same IP address we had to do port forwarding and this was quite difficult to find out. But in the end, was successful in deploying and was able to connect to Ollama from the internet. Even on local the API is taking around time to respond. Having a VPN with a static IP made it easier to achieve as the IP doesn't keep changing.