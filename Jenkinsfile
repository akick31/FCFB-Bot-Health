pipeline {
    agent any

    environment {
        IMAGE_NAME = 'fcfb-bot-health'
        CONTAINER_NAME = 'FCFB-Bot-Health'
        DOCKERFILE = 'Dockerfile'
        APP_PROPERTIES = "${env.WORKSPACE}/src/main/resources/application.properties"
        DISCORD_TOKEN = credentials('BOT_HEALTH_DISCORD_TOKEN')
        DISCORD_GUILD_ID = credentials('DISCORD_GUILD_ID')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out the Bot Health project...'
                checkout scm
            }
        }
        stage('Get Version') {
            steps {
                script {
                    // Get the latest Git tag
                    def latestTag = sh(script: "git describe --tags --abbrev=0", returnStdout: true).trim()

                    // If there are no tags, default to 1.0.0
                    if (!latestTag) {
                        latestTag = '1.0.0'
                    }

                    // Print the version
                    echo "Current Version: ${latestTag}"

                    // Set the version to an environment variable for use in later stages
                    env.VERSION = latestTag

                    // Set the build description
                    currentBuild.description = "Version: ${env.VERSION}"
                    currentBuild.displayName = "Build #${env.BUILD_NUMBER} - Version: ${env.VERSION}"
                }
            }
        }
        stage('Stop and Remove Existing Bot') {
            steps {
                script {
                    echo 'Stopping and removing the existing Bot Health instance...'
                    sh """
                        docker stop ${CONTAINER_NAME} || echo "Bot Health is not running."
                        docker rm ${CONTAINER_NAME} || echo "No old Bot Health instance to remove."
                    """
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Creating the properties file...'
                script {
                    def propertiesContent = """
                        # Discord configuration
                        discord.bot.token=${env.DISCORD_TOKEN}
                        discord.guild.id=${env.DISCORD_GUILD_ID}
                        discord.bot.id=1315920925601959986
                        discord.arceus.status.message.id=1315956000464834592
                        discord.refbot.status.message.id=1315956002054471712
                        discord.rotom.status.message.id=1315956002985742377

                        # Channel IDs
                        discord.health.channel.id=1314476014004604941

                        # Domain configuration
                        arceus.url=http://51.81.32.234:1212/arceus
                        refbot.url=http://51.81.32.234:1211/fcfb_discord

                        # Server configuration
                        server.port=1210
                    """.stripIndent()

                    writeFile file: "${env.APP_PROPERTIES}", text: propertiesContent
                }

                echo 'Building the Bot Health project...'
                sh './gradlew clean build'
            }
        }

        stage('Build New Docker Image') {
            steps {
                script {
                    echo 'Building the new Bot Health Docker image...'
                    sh """
                        docker build -t ${IMAGE_NAME}:${DOCKERFILE} .
                    """
                }
            }
        }

        stage('Run New Bot Health Container') {
            steps {
                script {
                    if (!fileExists("${env.APP_PROPERTIES}")) {
                        error("application.properties was not created!")
                    }

                    sh """
                        docker run --network="host" -d --restart=always --name ${CONTAINER_NAME} \\
                            -v ${env.APP_PROPERTIES}:/app/application.properties \\
                            ${IMAGE_NAME}:${DOCKERFILE}
                    """

                    sh """
                        docker exec ${CONTAINER_NAME} ls -la /app
                    """
                }
            }
        }

        stage('Cleanup Docker System') {
            steps {
                script {
                    echo 'Pruning unused Docker resources...'
                    sh 'docker system prune -a --force'
                }
            }
        }
    }

    post {
        success {
            echo 'Bot Health has been successfully deployed!'
        }
        failure {
            echo 'An error occurred during the Bot Health deployment.'
        }
    }
}
