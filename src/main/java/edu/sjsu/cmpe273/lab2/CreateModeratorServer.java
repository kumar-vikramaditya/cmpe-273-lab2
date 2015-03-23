package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class CreateModeratorServer {
  private static final Logger logger = Logger.getLogger(CreateModeratorServer.class.getName());

  /* The port on which the server should run */
  private int port = 50051;
  private ServerImpl server;
  private static long pollId = 7512345;

  private void start() throws Exception {
    server = NettyServerBuilder.forPort(port)
        .addService(PollServiceGrpc.bindService(new PollServiceImpl()))
        .build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        CreateModeratorServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws Exception {
    final CreateModeratorServer server = new CreateModeratorServer();
    server.start();
  }

  private class PollServiceImpl implements PollServiceGrpc.PollService {

    @Override
    public void createPoll(PollRequest req, StreamObserver<PollResponse> responseObserver) {
        String poll_Id = Long.toHexString(++pollId);
	logger.info("Creating a new Poll for moderator "+ req.getModeratorId());
		PollResponse reply = PollResponse.newBuilder().setId(poll_Id).build();
      responseObserver.onValue(reply);
      responseObserver.onCompleted();
    }
  }
}
                         
