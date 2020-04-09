node {
	properties(
		[parameters([
            choice(choices: 
				[
				'0.1', 
				'0.2', 
				'0.3', 
				'0.4', 
				'0.5',
				
				
				
				
				
			], 
		description: 'Which version of the app should I deploy? ', 
		name: 'Version'), 
	choice(choices: 
	[
		'dev1.gulmiradesign.com', 
		'qa1.gulmiradesign.com', 
		'stage1.gulmiradesign.com', 
		'prod1.gulmiradesign.com'], 
	description: 'Please provide an environment to build the application', 
	name: 'ENVIR')])])







		stage("Stage1"){
			timestamps {
				ws {
					checkout([$class: 'GitSCM', branches: [[name: '${Version}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/artemis.git']]])		}
			}
		}
		stage("Get Credentials"){
		timestamps {
			ws{
				sh '''
					     aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 679745294409.dkr.ecr.us-east-2.amazonaws.com/artemis
					'''
				}
			}
		}
		stage("Build Docker Image"){
		timestamps {
			ws {
				sh '''
					docker build -t artemis:${Version} .
					'''
				}
			}
		}
		stage("Tag Image"){
			timestamps {
				ws {
					sh '''
						docker tag artemis:${Version} 679745294409.dkr.ecr.us-east-2.amazonaws.com/artemis:${Version}
					'''
					}
				}
			}
		stage("Push Image"){
			timestamps {
				ws {
					sh '''
						docker push 679745294409.dkr.ecr.us-east-2.amazonaws.com/artemis:${Version}
						'''
				}
			}
		}
		stage("Send slack notifications"){
			timestamps {
				ws {
					echo "Slack"
					//slackSend color: '#BADA55', message: 'Hello, World!'
				}
			}
		}
		stage("Authenticate"){
			timestamps {
				ws {
					sh '''
						ssh centos@${ENVIR} $(aws ecr get-login --no-include-email --region us-east-2)
						'''
				}
			}
		}
		stage("Clean Up"){
			timestamps {
				ws {
					try {
						sh '''
							#!/bin/bash
							IMAGES=$(ssh centos@${ENVIR} docker ps -aq) 
							for i in \$IMAGES; do
								ssh centos@${ENVIR} docker stop \$i
								ssh centos@${ENVIR} docker rm \$i
							done 
							'''
					} catch(e) {
						println("Script failed with error: ${e}")
						}
					}
				}
			}
		
	stage("Run Container"){
		timestamps {
			ws {
				sh '''
					ssh centos@${ENVIR} docker run -dti -p 5001:5000 679745294409.dkr.ecr.us-east-2.amazonaws.com/artemis:${Version}
					'''
				}
			}
		}
}