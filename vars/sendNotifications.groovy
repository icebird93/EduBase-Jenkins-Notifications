#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESS'

  // Default values
  def color = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """<p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'BLUE'
    colorCode = '#5182C6'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else if (buildStatus == 'UNSTABLE') {
    color = '#FFFE89'
    colorCode = 'YELLOW'
  }

  // Send notifications
  if (! env.NOTIFICATION_SLACK_CHANNEL?.trim()) {
    slackSend (
      channel: "#${env.NOTIFICATION_SLACK_CHANNEL}",
      color: colorCode,
      message: summary
    )
  }

  // Send email (if variable is set)
  if (! env.NOTIFICATION_EMAIL_TO?.trim()) {
    emailext (
      to: "${env.NOTIFICATION_EMAIL_TO}",
      subject: subject,
      body: details,
      recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )
  }
}
