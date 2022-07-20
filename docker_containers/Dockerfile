FROM ubuntu:20.04
LABEL maintainer="masroorh"

# Avoid stuck build due to user prompt
ENV DEBIAN_FRONTEND="noninteractive" 
# Install OpenJDK 8
RUN \
    apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    rm -rf /var/lib/apt/lists/*

# Install vim  
RUN apt-get update && apt-get install -y vim iputils-ping

# Install Python

RUN apt-get update && apt-get install --no-install-recommends -y python3.8 python3.8-dev python3.8-venv python3-pip python3-wheel build-essential && \
    apt-get clean && rm -rf /var/lib/apt/lists/*


# Install PySpark and Numpy
RUN \
    pip install numpy && \
    pip install pyspark

# Define default command
ENTRYPOINT ["tail"]
CMD ["-f","/dev/null"]
