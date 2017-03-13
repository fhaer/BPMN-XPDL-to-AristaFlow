cd /D %~dp0
cd ..
java -cp BpmnAfTool.jar;test\junit.jar junit.swingui.TestRunner  de.bpmnaftool.control.BpmnAfToolImplTest
