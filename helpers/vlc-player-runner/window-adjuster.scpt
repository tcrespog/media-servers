tell application "System Events"
	set theProcesses to application processes
	set x to 0
	repeat with theProcess from 1 to count theProcesses
		tell process theProcess
			repeat with i from 1 to (count windows)
                if name of window i contains "video" then
				    set position of window i to {x, 200}
                    set x to x + 40
                end if
			end repeat
		end tell
	end repeat
end tell