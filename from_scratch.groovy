node {
    properties([
        // Below line sets "Discard builds more than 5"
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')),

        //Below line triggers this job every minute
       pipelineTriggers([pollSCM('* * * * *')])
       ])


stage("Pull Repo"){ 
    git 'https://github.com/farrukh90/cool_website.git' 
} 

stage("Install Prerequisites"){
		sh """
		ssh centos@jenkins_worker1.gulmiradesign.com                 sudo yum install httpd -y
		"""
} 
stage("Copy atrifacts"){
    sh """ 
    scp -r *  centos@jenkins_worker1.gulmiradesign.com:/tmp
	ssh centos@jenkins_worker1.gulmiradesign.com                 sudo cp -r /tmp/index.html /var/www/html/
	ssh centos@jenkins_worker1.gulmiradesign.com                 sudo cp -r /tmp/style.css /var/www/html/
	ssh centos@jenkins_worker1.gulmiradesign.com				   sudo chown centos:centos /var/www/html/
	ssh centos@jenkins_worker1.gulmiradesign.com				   sudo chmod 777 /var/www/html/*
	
	"""
}
		
stage("Restart web server"){ 
   sh "ssh centos@jenkins_worker1.gulmiradesign.com                 sudo systemctl restart httpd"  
} 
stage("Slack"){ 
   slackSend color: '#BADA55', message: 'Hello, World!'  
  }

} 
