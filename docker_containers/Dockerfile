FROM ubuntu:18.04
LABEL maintainer="masroorh"
ENV DEBIAN_FRONTEND="noninteractive" 
# Install OpenJDK 8
RUN \
    apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    rm -rf /var/lib/apt/lists/*
# Install vim  
RUN apt-get update && apt-get install -y vim iputils-ping
# Install Python
#RUN \
#    apt-get update && \
#    apt-get install -y python3 python3-pip && \
#    rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y software-properties-common gcc && \
    add-apt-repository -y ppa:deadsnakes/ppa && \
    rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y python3.8 python3-distutils python3-pip && \
    rm -rf /var/lib/apt/lists/*
#RUN apt-get update && \
#    apt-get install build-essential
#
#RUN apt-get update && \
#    apt-get install libssl-dev openssl && \
#    apt-get install -y wget && \
#    wget https://www.python.org/ftp/python/3.8.1/Python-3.8.1.tgz && \
#    tar xzvf Python-3.8.1.tgz && \
#    cd Python-3.8.1 && \
#    ./configure && \
#    make && \
#    make install && \
#
#
#RUN apt-get update && \
#    apt-get install -y python3-pip && \
#    rm -rf /var/lib/apt/lists/*
# Install PySpark and Numpy
RUN \
    pip install pip && \
    pip install numpy && \
    pip install pyspark
# Define default command
ENTRYPOINT ["tail"]
CMD ["-f","/dev/null"]
