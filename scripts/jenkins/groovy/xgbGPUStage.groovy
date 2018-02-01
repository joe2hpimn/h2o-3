def call(final pipelineContext, final stageConfig) {

    for (String component : stageConfig.additionalTestPackages) {
        pipelineContext.getUtils().unpackTestPackage(this, component, stageConfig.stageDir)
    }

    stageConfig.customBuildAction = """
        export PATH=/usr/lib/jvm/java-8-oracle/bin:/bin:/usr/local/nvidia/bin:/usr/local/cuda/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
        env
        cd ${WORKSPACE}/${stageConfig.stageDir}/h2o-3
        pwd
        ls -alh
        
        echo "Linking small and bigdata"
        rm -f smalldata
        ln -s -f /home/0xdiag/smalldata
        rm -f bigdata
        ln -s -f /home/0xdiag/bigdata
        
        if [ "${stageConfig.activatePythonEnv}" = 'true' ]; then
            echo "Activating Python ${stageConfig.pythonVersion}"
            . /envs/h2o_env_python${stageConfig.pythonVersion}/bin/activate
        fi
        
        echo "Running Make"
        
        unset CHANGE_AUTHOR_DISPLAY_NAME
        unset CHANGE_TITLE
        unset COMMIT_MESSAGE
        
        make -f ${stageConfig.makefilePath} ${stageConfig.target}
    """
    echo "BREAK 11"
    sh "nvidia-docker run --rm --init --pid host -u \$(id -u):\$(id -g) -v ${WORKSPACE}:${WORKSPACE} -v /home/0xdiag/:/home/0xdiag ${stageConfig.image} bash -c '''${stageConfig.customBuildAction}'''"
    echo "BREAK 12"
}

return this
