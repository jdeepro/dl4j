#-*-compile book pdf -*-#
all:
	xelatex dl4j.tex && xelatex dl4j.tex

demo:
	cd java && mvn compile

test:
	cd java && mvn -Dtest=$(target) test

# mvn clean
mvc:
	cd java && mvn clean

clean:
	@find . -maxdepth 2 -path .git -prune -type f \
			-o -name "*.aux" \
			-o -name "*.log" \
			-o -name "*.toc" \
			-o -name "*.backup" \
			-o -name "*.acr" \
			-o -name "*.brf" \
			-o -name "*.gz" \
			-o -name "*.acn" \
			-o -name "*.xdy" \
			-o -name "*.alg" \
			-o -name "*.idx" | xargs rm -f
